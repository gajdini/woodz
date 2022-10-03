package com.tooz.woodz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tooz.woodz.database.dao.ProjectDao
import com.tooz.woodz.database.entity.Project
import kotlinx.coroutines.flow.Flow

class ProjectViewModel(private val projectDao: ProjectDao): ViewModel(){
    fun allProjects(): Flow<List<Project>> = projectDao.getAll()
}

class ProjectViewModelFactory(private val projectDao: ProjectDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(projectDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}