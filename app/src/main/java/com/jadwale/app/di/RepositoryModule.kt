package com.jadwale.app.di

import com.jadwale.core.network.*
import com.jadwale.core.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(): ScheduleRepository = ApiScheduleRepository()

    @Provides
    @Singleton
    fun provideTeacherRepository(): TeacherRepository = ApiTeacherRepository()

    @Provides
    @Singleton
    fun provideClassRepository(): ClassRepository = ApiClassRepository()

    @Provides
    @Singleton
    fun provideSubjectRepository(): SubjectRepository = ApiSubjectRepository()

    @Provides
    @Singleton
    fun provideAssignmentRepository(): AssignmentRepository = ApiAssignmentRepository()

    @Provides
    @Singleton
    fun provideRoutineRepository(): RoutineRepository = ApiRoutineRepository()
}
