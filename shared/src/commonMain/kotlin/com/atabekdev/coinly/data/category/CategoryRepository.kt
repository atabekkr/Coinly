package com.atabekdev.coinly.data.category

import com.atabekdev.coinly.data.category.local.CategoryDao
import com.atabekdev.coinly.data.category.local.toModel
import com.atabekdev.coinly.data.category.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryRepository : KoinComponent {
    private val dao by inject<CategoryDao>()

    fun observeAll(): Flow<List<Category>> =
        dao.observeAll().map { categories -> categories.map { it.toModel() } }
}
