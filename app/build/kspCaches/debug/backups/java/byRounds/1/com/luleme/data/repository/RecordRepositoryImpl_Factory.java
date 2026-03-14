package com.luleme.data.repository;

import com.luleme.data.encryption.EncryptionManager;
import com.luleme.data.local.dao.RecordDao;
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
public final class RecordRepositoryImpl_Factory implements Factory<RecordRepositoryImpl> {
  private final Provider<RecordDao> daoProvider;

  private final Provider<EncryptionManager> encryptionManagerProvider;

  public RecordRepositoryImpl_Factory(Provider<RecordDao> daoProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    this.daoProvider = daoProvider;
    this.encryptionManagerProvider = encryptionManagerProvider;
  }

  @Override
  public RecordRepositoryImpl get() {
    return newInstance(daoProvider.get(), encryptionManagerProvider.get());
  }

  public static RecordRepositoryImpl_Factory create(Provider<RecordDao> daoProvider,
      Provider<EncryptionManager> encryptionManagerProvider) {
    return new RecordRepositoryImpl_Factory(daoProvider, encryptionManagerProvider);
  }

  public static RecordRepositoryImpl newInstance(RecordDao dao,
      EncryptionManager encryptionManager) {
    return new RecordRepositoryImpl(dao, encryptionManager);
  }
}
