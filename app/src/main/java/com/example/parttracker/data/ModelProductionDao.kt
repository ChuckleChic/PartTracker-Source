//package com.example.parttracker.data
//
//import androidx.room.*
//import com.example.parttracker.model.ModelProduction
//
//@Dao
//interface ModelProductionDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrUpdate(production: ModelProduction)
//
//    @Query("SELECT * FROM model_production WHERE model = :model AND date = :date AND shift = :shift LIMIT 1")
//    suspend fun getByModelAndDateShift(model: String, date: String, shift: String): ModelProduction?
//}

package com.example.parttracker.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parttracker.model.ModelProduction

@Dao
interface ModelProductionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(production: ModelProduction)


    @Query("SELECT * FROM model_production")
    suspend fun getAllModelProductions(): List<ModelProduction>


//    @Query("SELECT * FROM model_production WHERE model = :model AND date = :date AND shift = :shift LIMIT 1")
//    suspend fun getProduction(model: String, date: String, shift: String): ModelProduction?

    @Query("SELECT * FROM model_production WHERE model = :model AND color = :color AND date = :date AND shift = :shift LIMIT 1")
    suspend fun getModelProduction(
        model: String,
        color: String,
        date: String,
        shift: String
    ): ModelProduction?

}
