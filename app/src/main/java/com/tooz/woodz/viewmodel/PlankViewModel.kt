package com.tooz.woodz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tooz.woodz.database.dao.PlankDao
import com.tooz.woodz.database.entity.Plank
import kotlinx.coroutines.flow.Flow

class PlankViewModel(private val plankDao: PlankDao): ViewModel(){
    fun plankById(id: Int): Flow<Plank> = plankDao.getById(id)
    fun plankByBarcode(barcode: String): Flow<Plank> = plankDao.getByBarcode(barcode)
    fun planksByMaterialId(materialId: Int): Flow<List<Plank>> = plankDao.getAllByMaterial(materialId)
    fun plankIsDone(id: Int) = plankDao.setDone(id)
}

class PlankViewModelFactory(private val plankDao: PlankDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlankViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return PlankViewModel(plankDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}