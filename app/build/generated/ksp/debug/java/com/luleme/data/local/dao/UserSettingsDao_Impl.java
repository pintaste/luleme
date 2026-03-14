package com.luleme.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.luleme.data.local.entity.UserSettingsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserSettingsDao_Impl implements UserSettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserSettingsEntity> __insertionAdapterOfUserSettingsEntity;

  public UserSettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserSettingsEntity = new EntityInsertionAdapter<UserSettingsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_settings` (`id`,`age`,`birth_year`,`gender`,`lock_enabled`,`pin_hash`,`overview_type`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserSettingsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAge());
        if (entity.getBirthYear() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getBirthYear());
        }
        if (entity.getGender() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getGender());
        }
        final int _tmp = entity.getLockEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getPinHash() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPinHash());
        }
        if (entity.getOverviewType() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOverviewType());
        }
      }
    };
  }

  @Override
  public Object saveSettings(final UserSettingsEntity settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserSettingsEntity.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getSettings(final Continuation<? super UserSettingsEntity> $completion) {
    final String _sql = "SELECT * FROM user_settings WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserSettingsEntity>() {
      @Override
      @Nullable
      public UserSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfBirthYear = CursorUtil.getColumnIndexOrThrow(_cursor, "birth_year");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfLockEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "lock_enabled");
          final int _cursorIndexOfPinHash = CursorUtil.getColumnIndexOrThrow(_cursor, "pin_hash");
          final int _cursorIndexOfOverviewType = CursorUtil.getColumnIndexOrThrow(_cursor, "overview_type");
          final UserSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            final Integer _tmpBirthYear;
            if (_cursor.isNull(_cursorIndexOfBirthYear)) {
              _tmpBirthYear = null;
            } else {
              _tmpBirthYear = _cursor.getInt(_cursorIndexOfBirthYear);
            }
            final String _tmpGender;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmpGender = null;
            } else {
              _tmpGender = _cursor.getString(_cursorIndexOfGender);
            }
            final boolean _tmpLockEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfLockEnabled);
            _tmpLockEnabled = _tmp != 0;
            final String _tmpPinHash;
            if (_cursor.isNull(_cursorIndexOfPinHash)) {
              _tmpPinHash = null;
            } else {
              _tmpPinHash = _cursor.getString(_cursorIndexOfPinHash);
            }
            final String _tmpOverviewType;
            if (_cursor.isNull(_cursorIndexOfOverviewType)) {
              _tmpOverviewType = null;
            } else {
              _tmpOverviewType = _cursor.getString(_cursorIndexOfOverviewType);
            }
            _result = new UserSettingsEntity(_tmpId,_tmpAge,_tmpBirthYear,_tmpGender,_tmpLockEnabled,_tmpPinHash,_tmpOverviewType);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
