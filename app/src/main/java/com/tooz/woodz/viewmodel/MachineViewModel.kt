package com.tooz.woodz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tooz.woodz.database.dao.MachineDao
import com.tooz.woodz.database.entity.Machine
import kotlinx.coroutines.flow.Flow

class MachineViewModel(private val machineDao: MachineDao): ViewModel(){
    fun allMachines(): Flow<List<Machine>> = machineDao.getAll()
    fun machineIdByAddress(address: String): Flow<Int> = machineDao.getIdByAddress(address)
    fun allMachineAddresses(): Flow<List<String>> = machineDao.getAllAddresses()
}

class MachineViewModelFactory(private val machineDao: MachineDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MachineViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MachineViewModel(machineDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}