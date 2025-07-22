package com.skysoftsolution.basictoadavance.utility

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseFetchHelper {

    public const val PREF_NAME = "AppPrefs"  // <-- used only internally
    public const val LAST_FETCH_TIME_KEY_PREFIX = "LAST_FETCH_TIME_"
    public const val TWELVE_HOURS_IN_MILLIS = 12 * 60 * 60 * 1000L

    inline fun <reified T : Any> fetchDataIfNeeded(
        context: Context,
        db: FirebaseFirestore,
        collectionName: String,
        crossinline onInsert: (T) -> Unit,
        noinline onDeleteAll: (() -> Unit)? = null,
        noinline onComplete: (() -> Unit)? = null
    ) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)  // <--- used inside only
        val lastFetchTimeKey = LAST_FETCH_TIME_KEY_PREFIX + collectionName
        val lastFetchTime = sharedPref.getLong(lastFetchTimeKey, 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastFetchTime >= TWELVE_HOURS_IN_MILLIS) {
            db.collection(collectionName)
                .get()
                .addOnSuccessListener { result ->
                    onDeleteAll?.invoke()

                    for (document in result) {
                        val item = document.toObject(T::class.java)
                        onInsert(item)
                    }

                    sharedPref.edit().putLong(lastFetchTimeKey, currentTime).apply()

                    onComplete?.invoke()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onComplete?.invoke()
                }
        } else {
            onComplete?.invoke()
        }
    }
}

