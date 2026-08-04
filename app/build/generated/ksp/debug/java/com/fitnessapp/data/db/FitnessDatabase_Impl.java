package com.fitnessapp.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.fitnessapp.data.db.dao.FoodEntryDao;
import com.fitnessapp.data.db.dao.FoodEntryDao_Impl;
import com.fitnessapp.data.db.dao.SleepEntryDao;
import com.fitnessapp.data.db.dao.SleepEntryDao_Impl;
import com.fitnessapp.data.db.dao.StepsEntryDao;
import com.fitnessapp.data.db.dao.StepsEntryDao_Impl;
import com.fitnessapp.data.db.dao.UserGoalsDao;
import com.fitnessapp.data.db.dao.UserGoalsDao_Impl;
import com.fitnessapp.data.db.dao.WaterEntryDao;
import com.fitnessapp.data.db.dao.WaterEntryDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FitnessDatabase_Impl extends FitnessDatabase {
  private volatile FoodEntryDao _foodEntryDao;

  private volatile SleepEntryDao _sleepEntryDao;

  private volatile WaterEntryDao _waterEntryDao;

  private volatile StepsEntryDao _stepsEntryDao;

  private volatile UserGoalsDao _userGoalsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `calories` INTEGER NOT NULL, `proteinGrams` REAL NOT NULL, `carbsGrams` REAL NOT NULL, `fatGrams` REAL NOT NULL, `fiberGrams` REAL NOT NULL, `sugarGrams` REAL NOT NULL, `sodiumMg` REAL NOT NULL, `cholesterolMg` REAL NOT NULL, `mealType` TEXT NOT NULL, `dateMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startMillis` INTEGER NOT NULL, `endMillis` INTEGER NOT NULL, `quality` INTEGER NOT NULL, `notes` TEXT NOT NULL, `dateMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `water_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateMillis` INTEGER NOT NULL, `amountMl` INTEGER NOT NULL, `timestampMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `steps_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateMillis` INTEGER NOT NULL, `count` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_goals` (`id` INTEGER NOT NULL, `dailyCalorieGoal` INTEGER NOT NULL, `dailyProteinGoal` REAL NOT NULL, `dailyCarbsGoal` REAL NOT NULL, `dailyFatGoal` REAL NOT NULL, `dailyFiberGoal` REAL NOT NULL, `dailySugarGoal` REAL NOT NULL, `dailySodiumGoal` REAL NOT NULL, `dailyCholesterolGoal` REAL NOT NULL, `dailyWaterGoal` INTEGER NOT NULL, `dailySleepGoalHours` REAL NOT NULL, `dailyStepsGoal` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '340c587e6f431851ec8021d007f6abb5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `food_entries`");
        db.execSQL("DROP TABLE IF EXISTS `sleep_entries`");
        db.execSQL("DROP TABLE IF EXISTS `water_entries`");
        db.execSQL("DROP TABLE IF EXISTS `steps_entries`");
        db.execSQL("DROP TABLE IF EXISTS `user_goals`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFoodEntries = new HashMap<String, TableInfo.Column>(12);
        _columnsFoodEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("calories", new TableInfo.Column("calories", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("proteinGrams", new TableInfo.Column("proteinGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("carbsGrams", new TableInfo.Column("carbsGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("fatGrams", new TableInfo.Column("fatGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("fiberGrams", new TableInfo.Column("fiberGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("sugarGrams", new TableInfo.Column("sugarGrams", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("sodiumMg", new TableInfo.Column("sodiumMg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("cholesterolMg", new TableInfo.Column("cholesterolMg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("dateMillis", new TableInfo.Column("dateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFoodEntries = new TableInfo("food_entries", _columnsFoodEntries, _foreignKeysFoodEntries, _indicesFoodEntries);
        final TableInfo _existingFoodEntries = TableInfo.read(db, "food_entries");
        if (!_infoFoodEntries.equals(_existingFoodEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "food_entries(com.fitnessapp.data.db.entity.FoodEntry).\n"
                  + " Expected:\n" + _infoFoodEntries + "\n"
                  + " Found:\n" + _existingFoodEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsSleepEntries = new HashMap<String, TableInfo.Column>(6);
        _columnsSleepEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepEntries.put("startMillis", new TableInfo.Column("startMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepEntries.put("endMillis", new TableInfo.Column("endMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepEntries.put("quality", new TableInfo.Column("quality", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepEntries.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepEntries.put("dateMillis", new TableInfo.Column("dateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSleepEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSleepEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSleepEntries = new TableInfo("sleep_entries", _columnsSleepEntries, _foreignKeysSleepEntries, _indicesSleepEntries);
        final TableInfo _existingSleepEntries = TableInfo.read(db, "sleep_entries");
        if (!_infoSleepEntries.equals(_existingSleepEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "sleep_entries(com.fitnessapp.data.db.entity.SleepEntry).\n"
                  + " Expected:\n" + _infoSleepEntries + "\n"
                  + " Found:\n" + _existingSleepEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsWaterEntries = new HashMap<String, TableInfo.Column>(4);
        _columnsWaterEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterEntries.put("dateMillis", new TableInfo.Column("dateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterEntries.put("amountMl", new TableInfo.Column("amountMl", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWaterEntries.put("timestampMillis", new TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWaterEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWaterEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWaterEntries = new TableInfo("water_entries", _columnsWaterEntries, _foreignKeysWaterEntries, _indicesWaterEntries);
        final TableInfo _existingWaterEntries = TableInfo.read(db, "water_entries");
        if (!_infoWaterEntries.equals(_existingWaterEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "water_entries(com.fitnessapp.data.db.entity.WaterEntry).\n"
                  + " Expected:\n" + _infoWaterEntries + "\n"
                  + " Found:\n" + _existingWaterEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsStepsEntries = new HashMap<String, TableInfo.Column>(3);
        _columnsStepsEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepsEntries.put("dateMillis", new TableInfo.Column("dateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepsEntries.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStepsEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStepsEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStepsEntries = new TableInfo("steps_entries", _columnsStepsEntries, _foreignKeysStepsEntries, _indicesStepsEntries);
        final TableInfo _existingStepsEntries = TableInfo.read(db, "steps_entries");
        if (!_infoStepsEntries.equals(_existingStepsEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "steps_entries(com.fitnessapp.data.db.entity.StepsEntry).\n"
                  + " Expected:\n" + _infoStepsEntries + "\n"
                  + " Found:\n" + _existingStepsEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsUserGoals = new HashMap<String, TableInfo.Column>(12);
        _columnsUserGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyCalorieGoal", new TableInfo.Column("dailyCalorieGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyProteinGoal", new TableInfo.Column("dailyProteinGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyCarbsGoal", new TableInfo.Column("dailyCarbsGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyFatGoal", new TableInfo.Column("dailyFatGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyFiberGoal", new TableInfo.Column("dailyFiberGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailySugarGoal", new TableInfo.Column("dailySugarGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailySodiumGoal", new TableInfo.Column("dailySodiumGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyCholesterolGoal", new TableInfo.Column("dailyCholesterolGoal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyWaterGoal", new TableInfo.Column("dailyWaterGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailySleepGoalHours", new TableInfo.Column("dailySleepGoalHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyStepsGoal", new TableInfo.Column("dailyStepsGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserGoals = new TableInfo("user_goals", _columnsUserGoals, _foreignKeysUserGoals, _indicesUserGoals);
        final TableInfo _existingUserGoals = TableInfo.read(db, "user_goals");
        if (!_infoUserGoals.equals(_existingUserGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "user_goals(com.fitnessapp.data.db.entity.UserGoals).\n"
                  + " Expected:\n" + _infoUserGoals + "\n"
                  + " Found:\n" + _existingUserGoals);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "340c587e6f431851ec8021d007f6abb5", "e69e44690e2ecb8068c9931284d1f367");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "food_entries","sleep_entries","water_entries","steps_entries","user_goals");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `food_entries`");
      _db.execSQL("DELETE FROM `sleep_entries`");
      _db.execSQL("DELETE FROM `water_entries`");
      _db.execSQL("DELETE FROM `steps_entries`");
      _db.execSQL("DELETE FROM `user_goals`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FoodEntryDao.class, FoodEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SleepEntryDao.class, SleepEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WaterEntryDao.class, WaterEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StepsEntryDao.class, StepsEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserGoalsDao.class, UserGoalsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FoodEntryDao foodEntryDao() {
    if (_foodEntryDao != null) {
      return _foodEntryDao;
    } else {
      synchronized(this) {
        if(_foodEntryDao == null) {
          _foodEntryDao = new FoodEntryDao_Impl(this);
        }
        return _foodEntryDao;
      }
    }
  }

  @Override
  public SleepEntryDao sleepEntryDao() {
    if (_sleepEntryDao != null) {
      return _sleepEntryDao;
    } else {
      synchronized(this) {
        if(_sleepEntryDao == null) {
          _sleepEntryDao = new SleepEntryDao_Impl(this);
        }
        return _sleepEntryDao;
      }
    }
  }

  @Override
  public WaterEntryDao waterEntryDao() {
    if (_waterEntryDao != null) {
      return _waterEntryDao;
    } else {
      synchronized(this) {
        if(_waterEntryDao == null) {
          _waterEntryDao = new WaterEntryDao_Impl(this);
        }
        return _waterEntryDao;
      }
    }
  }

  @Override
  public StepsEntryDao stepsEntryDao() {
    if (_stepsEntryDao != null) {
      return _stepsEntryDao;
    } else {
      synchronized(this) {
        if(_stepsEntryDao == null) {
          _stepsEntryDao = new StepsEntryDao_Impl(this);
        }
        return _stepsEntryDao;
      }
    }
  }

  @Override
  public UserGoalsDao userGoalsDao() {
    if (_userGoalsDao != null) {
      return _userGoalsDao;
    } else {
      synchronized(this) {
        if(_userGoalsDao == null) {
          _userGoalsDao = new UserGoalsDao_Impl(this);
        }
        return _userGoalsDao;
      }
    }
  }
}
