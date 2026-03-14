package com.luleme.ui.screens.statistics;

import com.luleme.domain.repository.RecordRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class StatisticsViewModel_Factory implements Factory<StatisticsViewModel> {
  private final Provider<RecordRepository> recordRepositoryProvider;

  public StatisticsViewModel_Factory(Provider<RecordRepository> recordRepositoryProvider) {
    this.recordRepositoryProvider = recordRepositoryProvider;
  }

  @Override
  public StatisticsViewModel get() {
    return newInstance(recordRepositoryProvider.get());
  }

  public static StatisticsViewModel_Factory create(
      Provider<RecordRepository> recordRepositoryProvider) {
    return new StatisticsViewModel_Factory(recordRepositoryProvider);
  }

  public static StatisticsViewModel newInstance(RecordRepository recordRepository) {
    return new StatisticsViewModel(recordRepository);
  }
}
