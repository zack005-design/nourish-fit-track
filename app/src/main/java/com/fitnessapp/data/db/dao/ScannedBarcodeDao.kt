package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitnessapp.data.db.entity.ScannedBarcode

@Dao
interface ScannedBarcodeDao {

    @Query("SELECT * FROM scanned_barcodes WHERE barcode = :barcode LIMIT 1")
    fun getBarcode(barcode: String): ScannedBarcode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBarcode(barcode: ScannedBarcode): Long
}
