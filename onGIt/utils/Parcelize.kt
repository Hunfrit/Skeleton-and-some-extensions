package homecare.android.com.utils

import android.os.Parcel
import android.os.Parcelable

/**
 * Simple wrap on existing [Parcelable] interface to avoid overriding one excessive method
 */
interface Parcelize : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(parcel: Parcel, flags: Int)
}
