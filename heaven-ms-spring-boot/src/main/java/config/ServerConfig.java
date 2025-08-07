package config;

public class ServerConfig {
    //Database Configuration
    public String dbUrl;
    public String dbUser;
    public String dbPass;
    public int dbMaxConnect;

    public int channelLoad;
    public int channelLocks;

    public long monsterRespawn;
    public long timeTaskPurge;
    public long rankRefresh;
    public long couponRefresh;
    public long commonRefresh;

    public boolean picEnabled;
    public boolean pinEnabled;
    public int picExpiration;
    public int pinExpiration;

    public boolean automaticRegister;
    public boolean bcryptMigration;
    public boolean collectiveCharacterSlot;
    public boolean deterredMultiClient;

    //Besides blocking logging in with several client sessions on the same machine, this also blocks suspicious login attempts for players that tries to login on an account using several diferent remote addresses.

    //Multiclient Coordinator Configuration
    public int maxAllowedAccountHwId;
    public int maxAccountLoginAttempt;
    public int loginAttemptDuration;

    //Ip Configuration
    public String host;
    public boolean localServer;

    //Server Flags
    public boolean useCustomKeySet;
    public boolean useDebug;
    public String debugLevel;
    public boolean useThreadTracker;
    public boolean useSupplyRateCoupons;
    public boolean useIpValidation;
    public boolean useCharacterAccountCheck;

    public boolean useMaxRange;
    public boolean useMaxRangeEchoOfHero;
    public boolean useMts;
    /**
     * js脚本调用
     */
    public boolean useCpq;
    /**
     * js脚本调用
     */
    public boolean useEnableCustomNpcScript;
    public boolean useAutoHideGm;
    public boolean useBuybackSystem;
    public boolean useFixedRatioPoolUpdate;
    public boolean useFamilySystem;
    public boolean useDuey;
    public boolean useRandomizePoolGain;
    public boolean useItemSort;
    public boolean useItemSortByName;
    public boolean useParTyForStarters;
    public boolean useAutoAssignStartersAp;
    public boolean useAutoAssignSecondaryCap;
    public boolean useStartingAp4;
    public boolean useAutoBan;
    public boolean useAutoBanLog;
    public boolean useAutoSave;
    public boolean useServerAutoAssigner;
    public boolean useRefreshRankMove;
    public boolean useEnforceAdminAccount;
    public boolean useEnforceNoviceExpRate;
    public boolean useEnforceHpMpSwap;
    public boolean useEnforceMobLevelRange;
    public boolean useEnforceJobLevelRange;
    public boolean useEnforceJobSpRange;
    public boolean useEnforceItemSuggestion;
    public boolean useEnforceUnMerchAbleCash;
    public boolean useEnforceUnMerchAblePet;
    public boolean useEnforceMerchantSave;
    public boolean useEnforceMDoorPosition;
    public boolean useSpawnLootOnAnimation;
    public boolean useSpawnRelevantLoot;
    public boolean useErasePermitOnOpenShop;
    public boolean useEraseUnTradeAbleDrop;
    public boolean useErasePetOnExpiration;
    public boolean useBuffMostSignificant;
    public byte maxMonitoredBuffStats;
    public boolean useBuffEverlasting;
    public boolean useMultipleSameEquipDrop;
    public boolean useBanishAbleTownScroll;
    public boolean useEnableFullRespawn;
    public boolean useEnableChatLog;
    /**
     * js脚本调用
     */
    public boolean useRebirthSystem;
    public boolean useMapOwnershipSystem;
    public boolean USE_FISHING_SYSTEM;
    public boolean useNpcScriptable;

    /**
     * js脚本调用
     */
    public boolean useOldGmsStyledPqNpcs;
    /**
     * js脚本调用
     */
    public boolean useEnableSoloExpeditions;
    public boolean useEnableDailyExpeditions;
    public boolean useEnableRecallEvent;

    //Announcement Configuration
    public boolean useAnnounceShopItemSold;
    public boolean useAnnounceChangeJob;

    //Cash Shop Configuration
    public boolean useJointCashShopInventory;
    public boolean useClearOutdatedCoupons;
    public boolean allowCashShopNameChange;
    public boolean allowCashShopWorldTransfer;//Allows players to buy world transfers in the cash shop.

    //Maker Configuration
    public boolean useMakerPermissiveAtkUp;
    public boolean useMakerFeeHeuristics;

    /**
     * js脚本调用
     */
    public boolean useStarterMerge;

    //Commands Configuration
    public boolean blockGenerateCashItem;
    public boolean useWholeServerRanking;

    public double equipExpRate;
    public double pqBonusExpRate;

    public float partyBonusExpRate;
    public byte expSplitLevelInterval;
    public float expSplitMvpMod;
    public float expSplitCommonMod;

    //Miscellaneous Configuration
    public String timezone;
    public int maxAp;
    public int maxEventLevels;
    public long reopenNpcInterval;
    public int totMobQuestRequirement;
    public int mobReactorRefreshTime;
    public int partySearchReentryLimit;
    public long nameChangeCoolDown;
    public long worldTransferCoolDown;//Cooldown for world tranfers, default is same as name change (30 days).
    public boolean instantNameChange;

    //Dangling Items/Locks Configuration
    public int itemExpireTime;
    public int kiteExpireTime;
    public int itemMonitorTime;
    public int lockMonitorTime;

    //Map Monitor Configuration
    public int itemExpireCheck;
    public int itemLimitOnMap;
    public int mapVisitedSize;
    public int mapDamageOvertimeInterval;

    //Channel Mob Disease Monitor Configuration
    public int mobStatusMonitorProc;
    public int mobStatusMonitorLife;
    public int mobStatusAggroPersistence;
    public int mobStatusAggroInterval;
    public boolean useAutoAggroNearby;

    //Some Gameplay Enhancing Configurations
    //Scroll Configuration
    public boolean usePerfectGmScroll;
    public boolean usePerfectScrolling;
    public boolean useEnhancedChScroll;
    public boolean useEnhancedCrafting;
    public boolean useEnhancedClnSlate;
    /**
     * js脚本调用
     */
    public int scrollChanceRolls;
    public int chScrollStatRate;
    public int chScrollStatRange;

    //Beginner Skills Configuration
    public boolean useUltraNimbleFeet;
    public boolean useUltraRecovery;
    public boolean useUltraThreeSnails;

    /**
     * js脚本调用
     */
    public boolean useFullAranSkillSet;
    public boolean useFastReuseHeroWill;
    public boolean useAntiImmunityCrash;
    public boolean useUnDispelHolyShield;
    public boolean useFullHolySymbol;

    //Character Configuration
    public boolean useAddSlotsByLevel;
    public boolean useAddRatesByLevel;
    public boolean useStackCouponRates;
    public boolean usePerfectPitch;

    //Quest Configuration
    public boolean useQuestRate;

    //Quest Points Configuration
    public int questPointRepeatableInterval;
    public int questPointRequirement;
    public int questPointPerQuestComplete;
    public int questPointPerEventClear;

    //Guild Configuration
    public int createGuildMinPartners;
    public int createGuildCost;
    public int changeEmblemCost;
    public int expandGuildBaseCost;
    public int expandGuildTierCost;
    public int expandGuildMaxCost;

    //Family Configuration
    public int familyRepPerKill;
    public int familyRepPerBossKill;
    public int familyRepPerLevelUp;
    public int familyMaxGenerations;

    //Equipment Configuration
    public boolean useEquipmentLevelUpSlots;
    public boolean useEquipmentLevelUpPower;
    public boolean useEquipmentLevelUpCash;
    public boolean useSpikesAvoidBanish;
    public int maxEquipmentLevelUpStatUp;
    public int maxEquipmentStat;
    public int useEquipmentLevelUp;

    //Map-Chair Configuration
    public boolean useChairExtraHeal;
    public byte chairExtraHealMultiplier;
    public int chairExtraHealMaxDelay;

    //Player NPC Configuration
    public int playerNpcInitialX;
    public int playerNpcInitialY;
    public int playerNpcAreaX;
    public int playerNpcAreaY;
    public int playerNpcAreaSteps;
    public boolean playerNpcOrganizeArea;
    public boolean playerNpcAutoDeploy;

    //Pet Auto-Pot Configuration
    public boolean useCompulsoryAutoPot;
    public double petAutoHpRatio;
    public double petAutoMpRatio;

    //Pet & Mount Configuration
    public byte petExhaustCount;
    public byte mountExhaustCount;

    //Pet Hunger Configuration
    public boolean petsNeverHungry;
    public boolean gmPetsNeverHungry;

    //Event Configuration
    public int eventMaxGuildQueue;
    public long eventLobbyDelay;

    /**
     * js脚本调用
     */
    public boolean useFastDojoUpgrade;
    public boolean useDeadlyDojo;
    public int dojoEnergyAtk;
    public int dojoEnergyDmg;

    //Wedding Configuration
    public int weddingReservationDelay;
    public int weddingReservationTimeout;
    public int weddingReservationInterval;
    /**
     * js脚本调用
     */
    public int weddingBlessExp;
    public int weddingGiftLimit;
    /**
     * js脚本调用
     */
    public boolean weddingBlesserShowFx;

    //Buyback Configuration
    public boolean useBuybackWithMesos;
    public float buybackFee;
    public float buybackLevelStackFee;
    public int buybackMesoMultiplier;
    public int buybackReturnMinutes;
    public int buybackCoolDownMinutes;

    // Login timeout by shavit
    public long timeoutDuration;

    //Event End Timestamp
    public long eventEndTimestamp;

    public int buyFameFee;

    public int bmMobRate;
    public int useEnforceSpecifyLevelExpRate;

    public boolean playerLevelUpBroadcast;

    public boolean isKnightsOfCygnus;

    public boolean isAran;

    /**
     * 雇佣商店增加的经验率
     */
    public double hiredMerchantExpRate;

    /**
     * 捡起的装备上下浮动的值
     */
    public int pickedEquipRandomValue;

    /**
     * 捡起的装备上下浮动开启的等级
     */
    public int pickedEquipRandomOpenLevel;
}
