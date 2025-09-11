package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the NEW_MERCHANT_TABLE database table.
 * 
 */
@Entity
@Table(name="NEW_MERCHANT_TABLE")
@NamedQuery(name="NewMerchantTable.findAll", query="SELECT n FROM NewMerchantTable n")
public class NewMerchantTable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACC_TOKEN_RELATIONSHIP")
	private String accTokenRelationship;

	@Column(name="ACCESS_CODE")
	private String accessCode;

	@Column(name="ACCOUNT_SERVICE")
	private String accountService;

	private String address;

	@Column(name="APG_ALIAS")
	private String apgAlias;

	@Column(name="APG_SERVICE")
	private String apgService;

	@Column(name="APG_SETT_ACC")
	private String apgSettAcc;

	@Column(name="APG_SETT_BANK")
	private String apgSettBank;

	@Column(name="APG_SETT_METHOD")
	private String apgSettMethod;

	@Column(name="APPLEPAY_SERVICE")
	private String applepayService;

	@Column(name="AUTO_PAY")
	private String autoPay;

	@Column(name="AUTO_PAY_AMOUNT")
	private BigDecimal autoPayAmount;

	@Column(name="BUSINESS_NAME")
	private String businessName;

	@Column(name="CARD_TOKEN_RELATIONSHIP")
	private String cardTokenRelationship;

	@Column(name="CARD_TYPE")
	private String cardType;

	@Column(name="CASHIN_SERVICE")
	private String cashinService;

	private String category;

	@Column(name="CHECK_WHITELIST_CARD")
	private String checkWhitelistCard;

	private String city;

	@Column(name="CLIENT_ID")
	private String clientId;

	@Column(name="CLIENT_SECRET")
	private String clientSecret;

	private String code;

	@Column(name="CONTRACT_MERCHANT_CODE")
	private String contractMerchantCode;

	@Column(name="CONTRACT_MERCHANT_NAME")
	private String contractMerchantName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="CREATED_USER")
	private String createdUser;

	@Column(name="CURRENCY_DEFAULT")
	private String currencyDefault;

	@Column(name="DEEPLINK_SERVICE")
	private String deeplinkService;

	@Column(name="DEPLOY_MODEL")
	private String deployModel;

	@Column(name="DOM_ACQ_ACCOUNT_ID")
	private String domAcqAccountId;

	@Column(name="DOM_ACQ_CODE")
	private String domAcqCode;

	@Column(name="DOM_MCC")
	private String domMcc;

	@Column(name="DOM_STATUS")
	private String domStatus;

	@Column(name="DOM_VERSION")
	private String domVersion;

	@Column(name="ECOM_SERVICE")
	private String ecomService;

	@Column(name="ECOM2BEN_SERVICE")
	private String ecom2benService;

	private String email;

	@Column(name="ENCRYPT_STATUS")
	private String encryptStatus;

	@Column(name="FAST_PAY")
	private String fastPay;

	@Column(name="FAST_PAY_AMOUNT")
	private BigDecimal fastPayAmount;

	private String fax;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="GOLIVE_DATE")
	private Date goliveDate;

	@Column(name="HOME_PAGE")
	private String homePage;

	@Column(name="INSTALLMENT_SERVICE")
	private String installmentService;

	@Column(name="INT_ACQ_CODE")
	private String intAcqCode;

	@Column(name="INT_CARD_TOKEN_RLT")
	private String intCardTokenRlt;

	@Column(name="INT_DEPLOY_MODEL")
	private String intDeployModel;

	@Column(name="INT_ECOM_SERVICE")
	private String intEcomService;

	@Column(name="INT_MCC")
	private String intMcc;

	@Column(name="INT_MIGS_ACCESS_CODE")
	private String intMigsAccessCode;

	@Column(name="INT_MIGS_MERCHANT_ID")
	private String intMigsMerchantId;

	@Column(name="INT_MIGS_MERCHANT_PASSWORD")
	private String intMigsMerchantPassword;

	@Column(name="INT_MIGS_MERCHANT_REFUND_PW")
	private String intMigsMerchantRefundPw;

	@Column(name="INT_MIGS_MERCHANT_REFUND_USER")
	private String intMigsMerchantRefundUser;

	@Column(name="INT_MIGS_MERCHANT_USERNAME")
	private String intMigsMerchantUsername;

	@Column(name="INT_MIGS_MERCHANT_VOID_PW")
	private String intMigsMerchantVoidPw;

	@Column(name="INT_MIGS_MERCHANT_VOID_USER")
	private String intMigsMerchantVoidUser;

	@Column(name="INT_MIGS_PASSWORD")
	private String intMigsPassword;

	@Column(name="INT_MIGS_REFUND_PW")
	private String intMigsRefundPw;

	@Column(name="INT_MIGS_REFUND_USER")
	private String intMigsRefundUser;

	@Column(name="INT_MIGS_SECURE_HASH")
	private String intMigsSecureHash;

	@Column(name="INT_MIGS_USERNAME")
	private String intMigsUsername;

	@Column(name="INT_MIGS_VOID_PW")
	private String intMigsVoidPw;

	@Column(name="INT_MIGS_VOID_USER")
	private String intMigsVoidUser;

	@Column(name="INT_MPGS_ACQ_ACCOUNT_ID")
	private String intMpgsAcqAccountId;

	@Column(name="INT_MPGS_MERCHANT_ID")
	private String intMpgsMerchantId;

	@Column(name="INT_MPGS_MSO")
	private String intMpgsMso;

	@Column(name="INT_STATUS")
	private String intStatus;

	@Column(name="INT_TOKEN_SERVICE")
	private String intTokenService;

	@Column(name="INT_VERSION")
	private String intVersion;

	@Column(name="IPN_URL")
	private String ipnUrl;

	@Column(name="IPN_URL_RSA")
	private String ipnUrlRsa;

	private String kyc;

	@Column(name="LINK_EW")
	private String linkEw;

	@Column(name="LOCALE_DEFAULT")
	private String localeDefault;

	@Column(name="MERCHANT_ALIAS")
	private String merchantAlias;

	@Column(name="MERCHANT_ID")
	private BigDecimal merchantId;

	@Column(name="MERCHANT_PASSWORD")
	private String merchantPassword;

	@Column(name="MERCHANT_URL_RETURN")
	private String merchantUrlReturn;

	@Column(name="MERCHANT_USERNAME")
	private String merchantUsername;

	@Column(name="MPGS_API_PASSWORD")
	private String mpgsApiPassword;

	@Column(name="MPGS_API_USERNAME")
	private String mpgsApiUsername;

	private String name;

	@Column(name="OFFLINE_VCB")
	private BigDecimal offlineVcb;

	@Column(name="ONL_MERCHANT_ID")
	private BigDecimal onlMerchantId;

	@Column(name="ORDER_EXPIRED")
	private String orderExpired;

	@Column(name="OTP_TRANX_AMOUNT")
	private BigDecimal otpTranxAmount;

	@Column(name="PG_CODE")
	private String pgCode;

	private String ppt;

	@Column(name="PSP_CODE")
	private String pspCode;

	@Column(name="QR_IBFT")
	private String qrIbft;

	@Column(name="QRCODE_SERVICE")
	private String qrcodeService;

	@Column(name="QRIBFT_PLUS")
	private String qribftPlus;

	@Column(name="REPORT_WITH_SUB_ACCOUNT")
	private String reportWithSubAccount;

	@Column(name="REPOSITORY_ID")
	private String repositoryId;

	@Column(name="SECRET_KEY")
	private String secretKey;

	private String status;

	private String tel;

	@Column(name="TOKEN_FORMAT")
	private String tokenFormat;

	@Column(name="TOKEN_SERVICE")
	private String tokenService;

	@Column(name="TOKEN_USING_STATUS")
	private String tokenUsingStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	@Column(name="UPDATED_USER")
	private String updatedUser;

	@Column(name="URL_ISSUER_LOGO")
	private String urlIssuerLogo;

	@Column(name="URL_LOGO")
	private String urlLogo;

	@Column(name="URL_LOGO_MOBILE")
	private String urlLogoMobile;

	@Column(name="URL_RETURN")
	private String urlReturn;

	@Column(name="VAS_SERVICE")
	private String vasService;

	@Column(name="VERIFICATION_STRATEGY")
	private String verificationStrategy;

	@Column(name="VERIFICATION_TYPE")
	private String verificationType;

	@Column(name="WHITELABEL_SERVICE")
	private String whitelabelService;

	public NewMerchantTable() {
	}

	public String getAccTokenRelationship() {
		return this.accTokenRelationship;
	}

	public void setAccTokenRelationship(String accTokenRelationship) {
		this.accTokenRelationship = accTokenRelationship;
	}

	public String getAccessCode() {
		return this.accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getAccountService() {
		return this.accountService;
	}

	public void setAccountService(String accountService) {
		this.accountService = accountService;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getApgAlias() {
		return this.apgAlias;
	}

	public void setApgAlias(String apgAlias) {
		this.apgAlias = apgAlias;
	}

	public String getApgService() {
		return this.apgService;
	}

	public void setApgService(String apgService) {
		this.apgService = apgService;
	}

	public String getApgSettAcc() {
		return this.apgSettAcc;
	}

	public void setApgSettAcc(String apgSettAcc) {
		this.apgSettAcc = apgSettAcc;
	}

	public String getApgSettBank() {
		return this.apgSettBank;
	}

	public void setApgSettBank(String apgSettBank) {
		this.apgSettBank = apgSettBank;
	}

	public String getApgSettMethod() {
		return this.apgSettMethod;
	}

	public void setApgSettMethod(String apgSettMethod) {
		this.apgSettMethod = apgSettMethod;
	}

	public String getApplepayService() {
		return this.applepayService;
	}

	public void setApplepayService(String applepayService) {
		this.applepayService = applepayService;
	}

	public String getAutoPay() {
		return this.autoPay;
	}

	public void setAutoPay(String autoPay) {
		this.autoPay = autoPay;
	}

	public BigDecimal getAutoPayAmount() {
		return this.autoPayAmount;
	}

	public void setAutoPayAmount(BigDecimal autoPayAmount) {
		this.autoPayAmount = autoPayAmount;
	}

	public String getBusinessName() {
		return this.businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getCardTokenRelationship() {
		return this.cardTokenRelationship;
	}

	public void setCardTokenRelationship(String cardTokenRelationship) {
		this.cardTokenRelationship = cardTokenRelationship;
	}

	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCashinService() {
		return this.cashinService;
	}

	public void setCashinService(String cashinService) {
		this.cashinService = cashinService;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCheckWhitelistCard() {
		return this.checkWhitelistCard;
	}

	public void setCheckWhitelistCard(String checkWhitelistCard) {
		this.checkWhitelistCard = checkWhitelistCard;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContractMerchantCode() {
		return this.contractMerchantCode;
	}

	public void setContractMerchantCode(String contractMerchantCode) {
		this.contractMerchantCode = contractMerchantCode;
	}

	public String getContractMerchantName() {
		return this.contractMerchantName;
	}

	public void setContractMerchantName(String contractMerchantName) {
		this.contractMerchantName = contractMerchantName;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getCurrencyDefault() {
		return this.currencyDefault;
	}

	public void setCurrencyDefault(String currencyDefault) {
		this.currencyDefault = currencyDefault;
	}

	public String getDeeplinkService() {
		return this.deeplinkService;
	}

	public void setDeeplinkService(String deeplinkService) {
		this.deeplinkService = deeplinkService;
	}

	public String getDeployModel() {
		return this.deployModel;
	}

	public void setDeployModel(String deployModel) {
		this.deployModel = deployModel;
	}

	public String getDomAcqAccountId() {
		return this.domAcqAccountId;
	}

	public void setDomAcqAccountId(String domAcqAccountId) {
		this.domAcqAccountId = domAcqAccountId;
	}

	public String getDomAcqCode() {
		return this.domAcqCode;
	}

	public void setDomAcqCode(String domAcqCode) {
		this.domAcqCode = domAcqCode;
	}

	public String getDomMcc() {
		return this.domMcc;
	}

	public void setDomMcc(String domMcc) {
		this.domMcc = domMcc;
	}

	public String getDomStatus() {
		return this.domStatus;
	}

	public void setDomStatus(String domStatus) {
		this.domStatus = domStatus;
	}

	public String getDomVersion() {
		return this.domVersion;
	}

	public void setDomVersion(String domVersion) {
		this.domVersion = domVersion;
	}

	public String getEcomService() {
		return this.ecomService;
	}

	public void setEcomService(String ecomService) {
		this.ecomService = ecomService;
	}

	public String getEcom2benService() {
		return this.ecom2benService;
	}

	public void setEcom2benService(String ecom2benService) {
		this.ecom2benService = ecom2benService;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEncryptStatus() {
		return this.encryptStatus;
	}

	public void setEncryptStatus(String encryptStatus) {
		this.encryptStatus = encryptStatus;
	}

	public String getFastPay() {
		return this.fastPay;
	}

	public void setFastPay(String fastPay) {
		this.fastPay = fastPay;
	}

	public BigDecimal getFastPayAmount() {
		return this.fastPayAmount;
	}

	public void setFastPayAmount(BigDecimal fastPayAmount) {
		this.fastPayAmount = fastPayAmount;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Date getGoliveDate() {
		return this.goliveDate;
	}

	public void setGoliveDate(Date goliveDate) {
		this.goliveDate = goliveDate;
	}

	public String getHomePage() {
		return this.homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getInstallmentService() {
		return this.installmentService;
	}

	public void setInstallmentService(String installmentService) {
		this.installmentService = installmentService;
	}

	public String getIntAcqCode() {
		return this.intAcqCode;
	}

	public void setIntAcqCode(String intAcqCode) {
		this.intAcqCode = intAcqCode;
	}

	public String getIntCardTokenRlt() {
		return this.intCardTokenRlt;
	}

	public void setIntCardTokenRlt(String intCardTokenRlt) {
		this.intCardTokenRlt = intCardTokenRlt;
	}

	public String getIntDeployModel() {
		return this.intDeployModel;
	}

	public void setIntDeployModel(String intDeployModel) {
		this.intDeployModel = intDeployModel;
	}

	public String getIntEcomService() {
		return this.intEcomService;
	}

	public void setIntEcomService(String intEcomService) {
		this.intEcomService = intEcomService;
	}

	public String getIntMcc() {
		return this.intMcc;
	}

	public void setIntMcc(String intMcc) {
		this.intMcc = intMcc;
	}

	public String getIntMigsAccessCode() {
		return this.intMigsAccessCode;
	}

	public void setIntMigsAccessCode(String intMigsAccessCode) {
		this.intMigsAccessCode = intMigsAccessCode;
	}

	public String getIntMigsMerchantId() {
		return this.intMigsMerchantId;
	}

	public void setIntMigsMerchantId(String intMigsMerchantId) {
		this.intMigsMerchantId = intMigsMerchantId;
	}

	public String getIntMigsMerchantPassword() {
		return this.intMigsMerchantPassword;
	}

	public void setIntMigsMerchantPassword(String intMigsMerchantPassword) {
		this.intMigsMerchantPassword = intMigsMerchantPassword;
	}

	public String getIntMigsMerchantRefundPw() {
		return this.intMigsMerchantRefundPw;
	}

	public void setIntMigsMerchantRefundPw(String intMigsMerchantRefundPw) {
		this.intMigsMerchantRefundPw = intMigsMerchantRefundPw;
	}

	public String getIntMigsMerchantRefundUser() {
		return this.intMigsMerchantRefundUser;
	}

	public void setIntMigsMerchantRefundUser(String intMigsMerchantRefundUser) {
		this.intMigsMerchantRefundUser = intMigsMerchantRefundUser;
	}

	public String getIntMigsMerchantUsername() {
		return this.intMigsMerchantUsername;
	}

	public void setIntMigsMerchantUsername(String intMigsMerchantUsername) {
		this.intMigsMerchantUsername = intMigsMerchantUsername;
	}

	public String getIntMigsMerchantVoidPw() {
		return this.intMigsMerchantVoidPw;
	}

	public void setIntMigsMerchantVoidPw(String intMigsMerchantVoidPw) {
		this.intMigsMerchantVoidPw = intMigsMerchantVoidPw;
	}

	public String getIntMigsMerchantVoidUser() {
		return this.intMigsMerchantVoidUser;
	}

	public void setIntMigsMerchantVoidUser(String intMigsMerchantVoidUser) {
		this.intMigsMerchantVoidUser = intMigsMerchantVoidUser;
	}

	public String getIntMigsPassword() {
		return this.intMigsPassword;
	}

	public void setIntMigsPassword(String intMigsPassword) {
		this.intMigsPassword = intMigsPassword;
	}

	public String getIntMigsRefundPw() {
		return this.intMigsRefundPw;
	}

	public void setIntMigsRefundPw(String intMigsRefundPw) {
		this.intMigsRefundPw = intMigsRefundPw;
	}

	public String getIntMigsRefundUser() {
		return this.intMigsRefundUser;
	}

	public void setIntMigsRefundUser(String intMigsRefundUser) {
		this.intMigsRefundUser = intMigsRefundUser;
	}

	public String getIntMigsSecureHash() {
		return this.intMigsSecureHash;
	}

	public void setIntMigsSecureHash(String intMigsSecureHash) {
		this.intMigsSecureHash = intMigsSecureHash;
	}

	public String getIntMigsUsername() {
		return this.intMigsUsername;
	}

	public void setIntMigsUsername(String intMigsUsername) {
		this.intMigsUsername = intMigsUsername;
	}

	public String getIntMigsVoidPw() {
		return this.intMigsVoidPw;
	}

	public void setIntMigsVoidPw(String intMigsVoidPw) {
		this.intMigsVoidPw = intMigsVoidPw;
	}

	public String getIntMigsVoidUser() {
		return this.intMigsVoidUser;
	}

	public void setIntMigsVoidUser(String intMigsVoidUser) {
		this.intMigsVoidUser = intMigsVoidUser;
	}

	public String getIntMpgsAcqAccountId() {
		return this.intMpgsAcqAccountId;
	}

	public void setIntMpgsAcqAccountId(String intMpgsAcqAccountId) {
		this.intMpgsAcqAccountId = intMpgsAcqAccountId;
	}

	public String getIntMpgsMerchantId() {
		return this.intMpgsMerchantId;
	}

	public void setIntMpgsMerchantId(String intMpgsMerchantId) {
		this.intMpgsMerchantId = intMpgsMerchantId;
	}

	public String getIntMpgsMso() {
		return this.intMpgsMso;
	}

	public void setIntMpgsMso(String intMpgsMso) {
		this.intMpgsMso = intMpgsMso;
	}

	public String getIntStatus() {
		return this.intStatus;
	}

	public void setIntStatus(String intStatus) {
		this.intStatus = intStatus;
	}

	public String getIntTokenService() {
		return this.intTokenService;
	}

	public void setIntTokenService(String intTokenService) {
		this.intTokenService = intTokenService;
	}

	public String getIntVersion() {
		return this.intVersion;
	}

	public void setIntVersion(String intVersion) {
		this.intVersion = intVersion;
	}

	public String getIpnUrl() {
		return this.ipnUrl;
	}

	public void setIpnUrl(String ipnUrl) {
		this.ipnUrl = ipnUrl;
	}

	public String getIpnUrlRsa() {
		return this.ipnUrlRsa;
	}

	public void setIpnUrlRsa(String ipnUrlRsa) {
		this.ipnUrlRsa = ipnUrlRsa;
	}

	public String getKyc() {
		return this.kyc;
	}

	public void setKyc(String kyc) {
		this.kyc = kyc;
	}

	public String getLinkEw() {
		return this.linkEw;
	}

	public void setLinkEw(String linkEw) {
		this.linkEw = linkEw;
	}

	public String getLocaleDefault() {
		return this.localeDefault;
	}

	public void setLocaleDefault(String localeDefault) {
		this.localeDefault = localeDefault;
	}

	public String getMerchantAlias() {
		return this.merchantAlias;
	}

	public void setMerchantAlias(String merchantAlias) {
		this.merchantAlias = merchantAlias;
	}

	public BigDecimal getMerchantId() {
		return this.merchantId;
	}

	public void setMerchantId(BigDecimal merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantPassword() {
		return this.merchantPassword;
	}

	public void setMerchantPassword(String merchantPassword) {
		this.merchantPassword = merchantPassword;
	}

	public String getMerchantUrlReturn() {
		return this.merchantUrlReturn;
	}

	public void setMerchantUrlReturn(String merchantUrlReturn) {
		this.merchantUrlReturn = merchantUrlReturn;
	}

	public String getMerchantUsername() {
		return this.merchantUsername;
	}

	public void setMerchantUsername(String merchantUsername) {
		this.merchantUsername = merchantUsername;
	}

	public String getMpgsApiPassword() {
		return this.mpgsApiPassword;
	}

	public void setMpgsApiPassword(String mpgsApiPassword) {
		this.mpgsApiPassword = mpgsApiPassword;
	}

	public String getMpgsApiUsername() {
		return this.mpgsApiUsername;
	}

	public void setMpgsApiUsername(String mpgsApiUsername) {
		this.mpgsApiUsername = mpgsApiUsername;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getOfflineVcb() {
		return this.offlineVcb;
	}

	public void setOfflineVcb(BigDecimal offlineVcb) {
		this.offlineVcb = offlineVcb;
	}

	public BigDecimal getOnlMerchantId() {
		return this.onlMerchantId;
	}

	public void setOnlMerchantId(BigDecimal onlMerchantId) {
		this.onlMerchantId = onlMerchantId;
	}

	public String getOrderExpired() {
		return this.orderExpired;
	}

	public void setOrderExpired(String orderExpired) {
		this.orderExpired = orderExpired;
	}

	public BigDecimal getOtpTranxAmount() {
		return this.otpTranxAmount;
	}

	public void setOtpTranxAmount(BigDecimal otpTranxAmount) {
		this.otpTranxAmount = otpTranxAmount;
	}

	public String getPgCode() {
		return this.pgCode;
	}

	public void setPgCode(String pgCode) {
		this.pgCode = pgCode;
	}

	public String getPpt() {
		return this.ppt;
	}

	public void setPpt(String ppt) {
		this.ppt = ppt;
	}

	public String getPspCode() {
		return this.pspCode;
	}

	public void setPspCode(String pspCode) {
		this.pspCode = pspCode;
	}

	public String getQrIbft() {
		return this.qrIbft;
	}

	public void setQrIbft(String qrIbft) {
		this.qrIbft = qrIbft;
	}

	public String getQrcodeService() {
		return this.qrcodeService;
	}

	public void setQrcodeService(String qrcodeService) {
		this.qrcodeService = qrcodeService;
	}

	public String getQribftPlus() {
		return this.qribftPlus;
	}

	public void setQribftPlus(String qribftPlus) {
		this.qribftPlus = qribftPlus;
	}

	public String getReportWithSubAccount() {
		return this.reportWithSubAccount;
	}

	public void setReportWithSubAccount(String reportWithSubAccount) {
		this.reportWithSubAccount = reportWithSubAccount;
	}

	public String getRepositoryId() {
		return this.repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getSecretKey() {
		return this.secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTokenFormat() {
		return this.tokenFormat;
	}

	public void setTokenFormat(String tokenFormat) {
		this.tokenFormat = tokenFormat;
	}

	public String getTokenService() {
		return this.tokenService;
	}

	public void setTokenService(String tokenService) {
		this.tokenService = tokenService;
	}

	public String getTokenUsingStatus() {
		return this.tokenUsingStatus;
	}

	public void setTokenUsingStatus(String tokenUsingStatus) {
		this.tokenUsingStatus = tokenUsingStatus;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

	public String getUrlIssuerLogo() {
		return this.urlIssuerLogo;
	}

	public void setUrlIssuerLogo(String urlIssuerLogo) {
		this.urlIssuerLogo = urlIssuerLogo;
	}

	public String getUrlLogo() {
		return this.urlLogo;
	}

	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}

	public String getUrlLogoMobile() {
		return this.urlLogoMobile;
	}

	public void setUrlLogoMobile(String urlLogoMobile) {
		this.urlLogoMobile = urlLogoMobile;
	}

	public String getUrlReturn() {
		return this.urlReturn;
	}

	public void setUrlReturn(String urlReturn) {
		this.urlReturn = urlReturn;
	}

	public String getVasService() {
		return this.vasService;
	}

	public void setVasService(String vasService) {
		this.vasService = vasService;
	}

	public String getVerificationStrategy() {
		return this.verificationStrategy;
	}

	public void setVerificationStrategy(String verificationStrategy) {
		this.verificationStrategy = verificationStrategy;
	}

	public String getVerificationType() {
		return this.verificationType;
	}

	public void setVerificationType(String verificationType) {
		this.verificationType = verificationType;
	}

	public String getWhitelabelService() {
		return this.whitelabelService;
	}

	public void setWhitelabelService(String whitelabelService) {
		this.whitelabelService = whitelabelService;
	}

}