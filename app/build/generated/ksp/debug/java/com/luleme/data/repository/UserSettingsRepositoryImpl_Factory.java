package com.luleme.data.repository;

import com.luleme.data.local.dao.UserSettingsDao;
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
public final class UserSettingsRepositoryImpl_Factory implements Factory<UserSettingsRepositoryImpl> {
  private final Provider<UserSettingsDao> daoProvider;

  public UserSettingsRepositoryImpl_Factory(Provider<UserSettingsDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UserSettingsRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static UserSettingsRepositoryImpl_Factory create(Provider<UserSettingsDao> daoProvider) {
    return new UserSettingsRepositoryImpl_Factory(daoProvider);
  }

  public static UserSettingsRepositoryImpl newInstance(UserSettingsDao dao) {
    return new UserSettingsRepositoryImpl(dao);
  }
}
