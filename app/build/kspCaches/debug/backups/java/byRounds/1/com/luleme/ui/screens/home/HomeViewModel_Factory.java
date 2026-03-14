package com.luleme.ui.screens.home;

import com.luleme.domain.repository.RecordRepository;
import com.luleme.domain.repository.UserSettingsRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<RecordRepository> recordRepositoryProvider;

  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  public HomeViewModel_Factory(Provider<RecordRepository> recordRepositoryProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    this.recordRepositoryProvider = recordRepositoryProvider;
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(recordRepositoryProvider.get(), userSettingsRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<RecordRepository> recordRepositoryProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    return new HomeViewModel_Factory(recordRepositoryProvider, userSettingsRepositoryProvider);
  }

  public static HomeViewModel newInstance(RecordRepository recordRepository,
      UserSettingsRepository userSettingsRepository) {
    return new HomeViewModel(recordRepository, userSettingsRepository);
  }
}
