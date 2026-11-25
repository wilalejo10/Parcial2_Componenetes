package com.example.parcial2_componentes.domain.repository

import com.example.parcial2_componentes.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}