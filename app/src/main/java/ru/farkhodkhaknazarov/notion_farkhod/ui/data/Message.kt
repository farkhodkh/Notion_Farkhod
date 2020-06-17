package ru.farkhodkhaknazarov.notion_farkhod.ui.data

class Message(var belongsToCurrentUser: Boolean = false,
              var text: String = "",
              var member: Member) {

    fun isBelongsToCurrentUser(): Boolean{
        return belongsToCurrentUser
    }

    fun getMemberData(): Member{
        return member
    }
}