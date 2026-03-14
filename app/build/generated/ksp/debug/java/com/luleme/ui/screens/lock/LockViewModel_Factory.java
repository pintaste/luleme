package com.luleme.ui.screens.lock;

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
public final class LockViewModel_Factory implements Factory<LockViewModel> {
  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  public LockViewModel_Factory(Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
  }

  @Override
  public LockViewModel get() {
    return newInstance(userSettingsRepositoryProvider.get());
  }

  public static LockViewModel_Factory create(
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    return new LockViewModel_Factory(userSettingsRepositoryProvider);
  }

  public static LockViewModel newInstance(UserSettingsRepository userSettingsRepository) {
    return new LockViewModel(userSettingsRepository);
  }
}
