package com.luleme;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = LulemeApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface LulemeApplication_GeneratedInjector {
  void injectLulemeApplication(LulemeApplication lulemeApplication);
}
