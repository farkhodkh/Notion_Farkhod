package ru.farkhodkhaknazarov.notion_farkhod.ui.presenters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.CamcorderProfile
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.camera.AutoFitTextureView
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.FotoFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.FotoFragmentView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@InjectViewState
class FotoFragmentPresenter : MvpPresenter<FotoFragmentView>() {
    lateinit var fragmentView: View

    val TAG = "CameraFragment"
    val VIDEO_DIRECTORY_NAME = "AndroidWave"
    lateinit var mTextureView: AutoFitTextureView
    var mIsRecordingVideo: Boolean = false
    lateinit var mRecordVideo: ImageView
    lateinit var mVideoView: VideoView
    lateinit var mPlayVideo: ImageView
    var mOutputFilePath: String = "/sdcard/video.mp4"

    val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    val INVERSE_ORIENTATIONS = SparseIntArray()
    val DEFAULT_ORIENTATIONS = SparseIntArray()
    var mCurrentFile: File? = null
    var mCameraDevice: CameraDevice? = null
    var mPreviewSession: CameraCaptureSession? = null
    lateinit var mPreviewSize: Size
    lateinit var mVideoSize: Size
    var mMediaRecorder: MediaRecorder? = null
    var mBackgroundThread: HandlerThread? = null
    lateinit var mBackgroundHandler: Handler
    var mCameraOpenCloseLock: Semaphore = Semaphore(1)
    var mSensorOrientation: Int = 0
    lateinit var mPreviewBuilder: CaptureRequest.Builder

    var mStateCallback = (object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            startPreview()
            mCameraOpenCloseLock.release()
            if (null != mTextureView) {
                configureTransform(mTextureView.width, mTextureView.height)
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
            mCameraOpenCloseLock.release()
            camera.close()
            mCameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            camera.close()
            mCameraDevice = null
//            val activity: Activity = App.activity
//            activity?.finish()
        }

    })

    val mSurfaceTextureListener = (object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            var b = 0
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }
    })

    init {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270)

        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270)
    }

    override fun attachView(view: FotoFragmentView?) {
        super.attachView(view)

        fragmentView = (view as FotoFragment).fragmentView

        mRecordVideo = fragmentView.findViewById(R.id.mRecordVideo)
        mRecordVideo.setOnClickListener {
            onViewClicked(it)
        }
        mPlayVideo = fragmentView.findViewById(R.id.mPlayVideo)
        mPlayVideo.setOnClickListener {
            onViewClicked(it)
        }
        mTextureView = fragmentView.findViewById(getTextureResource())

        mVideoView = fragmentView.findViewById(R.id.mVideoView)
    }

    fun getTextureResource(): Int = R.id.mTextureView

    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.mRecordVideo ->
                if (mIsRecordingVideo) {
                    try {
                        stopRecordingVideo()
                        prepareViews()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    startRecordingVideo()
                    mRecordVideo.setImageResource(R.drawable.ic_stop)
                    //Receive out put file here
                    mOutputFilePath = getCurrentFile()?.absolutePath ?: ""
                }
            R.id.mPlayVideo -> {
                mVideoView.start()
                mPlayVideo.visibility = View.INVISIBLE
            }
            else -> print("Wrong button")
        }
    }

    fun prepareViews() {
        if (mVideoView.visibility == View.GONE) {
            mVideoView.visibility = View.VISIBLE
            mPlayVideo.visibility = View.VISIBLE
            mTextureView.visibility = View.GONE
            try {
                setMediaForRecordVideo()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun setMediaForRecordVideo() {
//        mOutputFilePath = parseVideo(mOutputFilePath)
        mVideoView.setMediaController(MediaController(App.activity))
        mVideoView.requestFocus()
        mVideoView.setVideoPath(mOutputFilePath)
        mVideoView.seekTo(100)
        mVideoView.setOnCompletionListener { mp: MediaPlayer? ->
            mVideoView.visibility = View.GONE
            mTextureView.visibility = View.VISIBLE
            mPlayVideo.visibility = View.GONE
            mRecordVideo.setImageResource(R.drawable.ic_record)
        }
    }

    fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int, aspectRatio: Size): Size {

        val bigEnough: MutableList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.height == option.width * h / w && option.width >= width && option.height >= height
            ) {
                bigEnough.add(option)
            }
        }

        return if (bigEnough.size > 0) {
            Collections.min(
                bigEnough,
                CompareSizesByArea()
            )
        } else {
            choices[0]
        }
    }

    fun onResumeView() {
        GlobalScope.launch(Dispatchers.Main) {

            if(App.premissionManager.isPermissionsGranted()==0){
                if (mTextureView.isAvailable) {
                    openCamera(mTextureView.width, mTextureView.height)
                } else {
                    mTextureView.surfaceTextureListener = mSurfaceTextureListener
                }
                startBackgroundThread()
            }
        }
    }

    fun onPauseView() {
        closeCamera()
        stopBackgroundThread()
    }

    fun getCurrentFile(): File? = mCurrentFile

    fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun openCamera(width: Int, height: Int) {
        if (App.activity.isFinishing) {
            return
        }

        if (ActivityCompat.checkSelfPermission(
                App.activity,
                Manifest.permission.CAMERA
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        try {
            if (!mCameraOpenCloseLock.tryAcquire(
                    2500,
                    TimeUnit.MILLISECONDS
                )
            ) {
                throw java.lang.RuntimeException("Time out waiting to lock camera opening.")
            }

            val manager =
                App.activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

            var cameraId: String = manager.cameraIdList.get(0)

            for (camId in manager.cameraIdList) {
                val camercameraCharaChar = manager.getCameraCharacteristics(camId)
                val facing = camercameraCharaChar.get(CameraCharacteristics.LENS_FACING)
                if (facing ==  CameraCharacteristics.LENS_FACING_FRONT){
                    cameraId = camId
                    break
                }
            }

            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
            if (map == null) {
                throw java.lang.RuntimeException("Cannot get available preview/video sizes")
            }
            mVideoSize = chooseVideoSize(
                map.getOutputSizes(
                    MediaRecorder::class.java
                )
            )

            mPreviewSize = chooseOptimalSize(
                map.getOutputSizes(
                    SurfaceTexture::class.java
                ),
                width, height, mVideoSize
            )

            val orientation = App.activity.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.width, mPreviewSize.height)
            } else {
                mTextureView.setAspectRatio(mPreviewSize.height, mPreviewSize.width)
            }
            configureTransform(width, height)
            mMediaRecorder = MediaRecorder()
            manager.openCamera(cameraId, mStateCallback, null)

        } catch (ex: CameraAccessException) {
            ex.printStackTrace()
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        } catch (ex: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.")
        }
    }

    fun chooseVideoSize(choices: Array<Size>): Size {
        for (size in choices) {
            if (1920 == size.width && 1080 == size.height) {
                return size
            }
        }
        for (size in choices) {
            if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                return size
            }
        }
        return choices[choices.size - 1]
    }

    fun getOutputMediaFile(): File? {

        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory(),
            VIDEO_DIRECTORY_NAME
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File

        mediaFile = File(
            mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4"
        )

        return mediaFile
    }

    fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            closePreviewSession()
            mCameraDevice?.close()
            mMediaRecorder?.release()
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException("Interrupted while trying to lock camera closing.")
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    fun startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable || null == mPreviewSize) {
            return
        }

        try {
            closePreviewSession()
            val texture = mTextureView.surfaceTexture!!
            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
            mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)!!
            val previewSurface = Surface(texture)
            mPreviewBuilder.addTarget(previewSurface)
            mCameraDevice?.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull session: CameraCaptureSession) {
                        mPreviewSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(@NonNull session: CameraCaptureSession) {
//                        Log.e(CameraVideoFragment.TAG, "onConfigureFailed: Failed ")
                    }
                }, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun updatePreview() {
        if (null == mCameraDevice) {
            return
        }

        try {
            setUpCaptureRequestBuilder(mPreviewBuilder)
            val thread = HandlerThread("CameraPreview")
            thread.start()
            mPreviewSession?.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun setUpCaptureRequestBuilder(builder: CaptureRequest.Builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    fun configureTransform(viewWidth: Int, viewHeight: Int) {
        if (null == mTextureView || null == mPreviewSize || null == App.activity) {
            return
        }
        val rotation = App.activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(
            0F, 0F,
            mPreviewSize.height.toFloat(),
            mPreviewSize.width.toFloat()
        )
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                viewHeight.toFloat() / mPreviewSize.height,
                viewWidth.toFloat() / mPreviewSize.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
        }
        mTextureView.setTransform(matrix)
    }

    fun setUpMediaRecorder() {
        if (null == App.activity||App.premissionManager.isPermissionsGranted()!=0) {
            return
        }

        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        mCurrentFile = getOutputMediaFile()!!
        mMediaRecorder?.setOutputFile(mCurrentFile?.absolutePath)
        val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P)
        mMediaRecorder?.setVideoFrameRate(profile.videoFrameRate)
        mMediaRecorder?.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight)
        mMediaRecorder?.setVideoEncodingBitRate(profile.videoBitRate)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mMediaRecorder?.setAudioEncodingBitRate(profile.audioBitRate)
        mMediaRecorder?.setAudioSamplingRate(profile.audioSampleRate)

        val rotation = App.activity.windowManager.defaultDisplay.rotation

        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES -> mMediaRecorder?.setOrientationHint(
                DEFAULT_ORIENTATIONS.get(rotation)
            )
            SENSOR_ORIENTATION_INVERSE_DEGREES -> mMediaRecorder?.setOrientationHint(
                INVERSE_ORIENTATIONS.get(rotation)
            )
        }
        mMediaRecorder?.prepare()
    }

    fun startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable || null == mPreviewSize) {
            return
        }

        try {
            closePreviewSession()
            setUpMediaRecorder()
            val texture = mTextureView.surfaceTexture!!
            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
            mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)!!
            val surfaces: MutableList<Surface> =
                ArrayList()

            val previewSurface = Surface(texture)
            surfaces.add(previewSurface)
            mPreviewBuilder.addTarget(previewSurface)

            //MediaRecorder setup for surface
            val recorderSurface = mMediaRecorder?.surface!!
            surfaces.add(recorderSurface)
            mPreviewBuilder.addTarget(recorderSurface)

            // Start a capture session
            mCameraDevice?.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        mPreviewSession = cameraCaptureSession
                        updatePreview()
                        App.activity.runOnUiThread {
                            mIsRecordingVideo = true
                            // Start recording
                            mMediaRecorder?.start()
                        }
                    }

                    override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {
//                        Log.e(CameraVideoFragment.TAG, "onConfigureFailed: Failed")
                    }
                },
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun closePreviewSession() {
        mPreviewSession?.close()
    }

    @Throws(Exception::class)
    open fun stopRecordingVideo() {

        mIsRecordingVideo = false
        try {
            mPreviewSession?.stopRepeating()
            mPreviewSession?.abortCaptures()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // Stop recording
        mMediaRecorder?.stop()
        mMediaRecorder?.reset()
    }

    class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            return java.lang.Long.signum(
                lhs.width as Long * lhs.height -
                        rhs.width as Long * rhs.height
            )
        }
    }
}