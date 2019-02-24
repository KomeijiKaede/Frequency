package net.teamfruit.frequency.util

import android.os.AsyncTask
import android.util.Log
import net.teamfruit.frequency.database.Base
import net.teamfruit.frequency.database.DBEntity

class AddAsyncTask(private val base: Base): AsyncTask<DBEntity, Void, Void>() {
    override fun doInBackground(vararg params: DBEntity): Void? {
        base.dbdao().insert(params[0])
        Log.d(AddAsyncTask::class.java.simpleName, params[0].title)
        return null
    }
}