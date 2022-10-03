package com.tooz.woodz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tooz.woodz.database.dao.MaterialDao
import com.tooz.woodz.database.entity.Material
import kotlinx.coroutines.flow.Flow

class MaterialViewModel(private val materialDao: MaterialDao): ViewModel(){
    fun allMaterials(): Flow<List<Material>> = materialDao.getAll()
    fun materialsByProjectId(projectId: Int): Flow<List<Material>> = materialDao.getAllByProject(projectId)
}

class MaterialViewModelFactory(private val materialDao: MaterialDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MaterialViewModel(materialDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}