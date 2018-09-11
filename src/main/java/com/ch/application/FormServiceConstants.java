package com.ch.application;

import com.ch.model.FormStatus;

import java.util.Locale;

/**
 * Created by Aaron.Witter on 15/07/2016.
 */
public class FormServiceConstants {

  public static final String DATE_TIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_TIME_FORMAT_SUBMISSION = "yyyyMMdd";

  public static final String PACKAGE_METADATA_NAME = "package";
  public static final String PACKAGE_STATUS_DEFAULT = FormStatus.PENDING.toString().toUpperCase(Locale.ENGLISH);
  public static final String PACKAGE_STATUS_NEEDS_PAYMENT = FormStatus.UNPAID.toString().toUpperCase(Locale.ENGLISH);
  
  public static final String PAYMENT_TYPE_ACCOUNT = "account";

  public static final String DATABASE_OBJECTID_KEY = "_id";
  public static final String DATABASE_FORMS_COLLECTION_NAME = "forms";
  public static final String DATABASE_PACKAGES_COLLECTION_NAME = "packages";
}
