package eu.musesproject.server.rt2ae;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 S2 Grupo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.ConnectionCallbacksImpl;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.correlator.engine.CorrelationStartupServlet;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class TestEventProcessorRt2aeIntegration extends TestCase{

	
	private final String defaultSessionId = "DFOOWE423423422H23H";
	private final String testFullCycleWithClues = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE//MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testSecurityDeviceStateStep1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894992,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Kaspersky Mobile Security, com.kaspersky.mobile.security;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	private final String testSecurityDeviceStateStep2 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testUserAction = "{\"behavior\":{\"action\":\"cancel\"},\"requesttype\":\"user_behavior\"}";
	//private final String testUserAction3 = "{\"behavior\":{\"action\":\"cancel\"},\"username\":\"muses\",\"device_id\":\"354401050109737\",\"requesttype\":\"user_behavior\",\"id\":1978}";
	private final String testUserAction3 = "{\"behavior\":{\"action\":\"cancel\"},\"username\":\"muses\",\"device_id\":\"354401050109737\",\"requesttype\":\"user_behavior\"}";
	
	
	private final String testOpenConfAssetInSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WEP\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenConfAssetSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testOpenConfAssetSecureReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1411663474535,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, net.openvpn.openvpn, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather, com.lge.lmk, com.lge.lgfotaclient:remote]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411663462078,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411663482228,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"2\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411663462220,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,216;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6109034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51001051;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1411663483486,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"\\/sdcard\\/SWE\\/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"appname\":\"Gmail\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testBlacklistAppWifiAnalyzer = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Wifi Analyzer\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.wifi.analyzer\",\"appname\":\"Wifi Analyzer\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testNotBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Other\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.other.app\",\"appname\":\"Other\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithoutAttachments = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 0,\"attachmentInfo\": \"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithAttachments = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 2,\"attachmentInfo\": \"name,type,size;name2,type2,size2\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithAttachmentsReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"3586480519805834fd9ccf61\",\"requesttype\":\"online_decision\"}";
	private final String testVirusFound = "{\"sensor\":{},\"action\":{\"type\":\"virus_found\",\"timestamp\" : \"1389885147\",\"properties\": {\"path\":\"/sdcard/SWE/virus.txt\",\"name\":\"seriour_virus\",\"severity\":\"high\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testVirusFoundReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_found\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/SWE\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";

	private final String testOpenAssetUC6_user_muses_door1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/SWE/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenAssetUC6_user_muses2_door1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/SWE/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenAssetUC6_user_muses3_door1 =	"{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/SWE/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";	
			
	private final String testScreenLockDisable = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410356356486,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410356610171,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"id\":\"1\",\"isrooted\":\"false\",\"isrootpermissiongiven\":\"false\",\"timestamp\":1410356610171,\"ipaddress\":\"172.17.1.52\",\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"true\",\"musesdatabaseexists\":\"true\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"2\",\"timestamp\":1410348330382,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;..._MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410356612042,\"type\":\"ACTION_SEND_MAIL\",\"properties\":{\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"subject\":\"MUSES sensor status subject\",\"path\":\"sdcard\",\"bcc\":\"hidden.reiceiver@generic.com\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testScreenLockDisableReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"120\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	private final String testScreenLockTimeoutReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,ch.cryptonite, Cryptonite,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"300\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	private final String testScreenLockDisableInAction = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410356356486,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410356610171,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"id\":\"1\",\"isrooted\":\"false\",\"isrootpermissiongiven\":\"false\",\"timestamp\":1410356610171,\"ipaddress\":\"172.17.1.52\",\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"true\",\"musesdatabaseexists\":\"true\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"2\",\"timestamp\":1410348330382,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;..._MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410356612042,\"type\":\"security_property_changed\",\"properties\":{\"property\":\"SCREEN_LOCK_TYPE\",\"value\":\"None\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testUninstallRequiredApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testDebug = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"1\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\\/MUSES_confidential_doc.txt\",\"timestamp\":1412171145047,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"open\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1412171050085,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"192.168.35.199\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][WPS][ESS]\",\"timestamp\":1412171065588,\"bssid\":\"00:1c:f0:f1:b1:08\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"3\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1412171049660,\"installedapps\":\"Android System,android,17;com.android.backupconfirm,com.android.backupconfirm,17;Bluetooth Share,com.android.bluetooth,17;com.android.browser.provider,com.android.browser.provider,17;Calculator,com.android.calculator2,17;Certificate Installer,com.android.certinstaller,17;Chrome,com.android.chrome,1025469;Contacts,com.android.contacts,17;Package Access Helper,com.android.defcontainer,17;Basic Daydreams,com.android.dreams.basic,17;Face Unlock,com.android.facelock,17;HTML Viewer,com.android.htmlviewer,17;Input Devices,com.android.inputdevices,17;Key Chain,com.android.keychain,17;Launcher,com.android.launcher,17;Fused Location,com.android.location.fused,17;MusicFX,com.android.musicfx,10400;Nfc Service,com.android.nfc,17;Bubbles,com.android.noisefield,1;Package installer,com.android.packageinstaller,17;Phase Beam,com.android.phasebeam,1;Mobile Data,com.android.phone,17;Search Applications Provider,com.android.providers.applications,17;Calendar Storage,com.android.providers.calendar,17;Contacts Storage,com.android.providers.contacts,17;Download Manager,com.android.providers.downloads,17;Downloads,com.android.providers.downloads.ui,17;DRM Protected Content Storage,com.android.providers.drm,17;Media Storage,com.android.providers.media,511;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,17;Settings Storage,com.android.providers.settings,17;Mobile Network Configuration,com.android.providers.telephony,17;User Dictionary,com.android.providers.userdictionary,17;Settings,com.android.settings,17;com.android.sharedstoragebackup,com.android.sharedstoragebackup,17;System UI,com.android.systemui,17;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,17;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral,17;Live Wallpaper Picker,com.android.wallpaper.livepicker,17;avast! Mobile Security,com.avast.android.mobilesecurity,7801;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,20729;Currents,com.google.android.apps.currents,130141211;Google Play Magazines,com.google.android.apps.magazines,130212123;Maps,com.google.android.apps.maps,614020503;Google+,com.google.android.apps.plus,351420604;Picasa Uploader,com.google.android.apps.uploader,224000;Wallet,com.google.android.apps.walletnfcrel,301;Google Backup Transport,com.google.android.backup,17;Calendar,com.google.android.calendar,201210290;ConfigUpdater,com.google.android.configupdater,17;Clock,com.google.android.deskclock,203;Sound Search for Google Play,com.google.android.ears,6;Email,com.google.android.email,410000;Exchange Services,com.google.android.exchange,500000;Market Feedback Agent,com.google.android.feedback,17;Gallery,com.google.android.gallery3d,40001;Gmail,com.google.android.gm,650;Google Play services,com.google.android.gms,6109036;Google Search,com.google.android.googlequicksearchbox,210020210;Google Services Framework,com.google.android.gsf,17;Google Account Manager,com.google.android.gsf.login,17;Google Korean keyboard,com.google.android.inputmethod.korean,83;Android keyboard,com.google.android.inputmethod.latin,1700;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack,170;Google Pinyin,com.google.android.inputmethod.pinyin,23;Network Location,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,83;Google Play Music,com.google.android.music,912;Google One Time Init,com.google.android.onetimeinitializer,17;Google Partner Setup,com.google.android.partnersetup,17;Setup Wizard,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Google Contacts Sync,com.google.android.syncadapters.contacts,17;Tags,com.google.android.tag,101;Talk,com.google.android.talk,330;Google Text-to-speech Engine,com.google.android.tts,17;Movie Studio,com.google.android.videoeditor,11;Google Play Movies & TV,com.google.android.videos,23079;com.google.android.voicesearch,com.google.android.voicesearch,40000000;YouTube,com.google.android.youtube,4216;Earth,com.google.earth,12352100;Quickoffice,com.qo.android.tablet.oem,3;_MUSES,eu.musesproject.client,1;MusesClientTestTest,eu.musesproject.client.test,1;iWnn IME,jp.co.omronsoft.iwnnime.ml,6;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white,1\",\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"avast! Mobile Security\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"7801\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1412171129236,\"appversion\":\"215\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.google.process.location, com.android.bluetooth, com.google.process.gapps, com.android.vending, com.google.android.talk, com.avast.android.mobilesecurity, com.android.bluetooth, com.google.android.inputmethod.latin, com.google.process.location, com.android.systemui, com.avast.android.mobilesecurity, com.avast.android.mobilesecurity, com.google.android.inputmethod.latin, com.google.android.gms.wearable, com.google.android.apps.maps:GoogleLocationService, com.avast.android.mobilesecurity, com.google.process.location, com.android.systemui, com.android.phone, com.estrongs.android.pop, com.google.process.location, com.android.bluetooth, com.android.location.fused, com.android.defcontainer, android.process.media, com.google.process.gapps, com.google.process.location, com.google.process.location, com.android.vending, com.avast.android.mobilesecurity, com.google.android.apps.maps, com.google.android.gms, com.google.process.gapps, com.android.bluetooth, eu.musesproject.client, com.google.process.location, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.nfc:handover, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.process.gapps, com.android.bluetooth]\",\"appname\":\"ES File Explorer\",\"packagename\":\"com.estrongs.android.pop\"}},\"action\":{\"timestamp\":1412171145047,\"type\":\"open_asset\",\"properties\":{\"id\":\"1\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\\/MUSES_confidential_doc.txt\",\"fileevent\":\"open\"}},\"username\":\"muses\",\"device_id\":\"e3da52dbe610b684\",\"requesttype\":\"online_decision\"}";
	
	private final String testSaveFileInMonitoredFolder = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"save_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testOpenFileInMonitoredFolder = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testUninstall = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413548015422,\"appversion\":\"16\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Instalador de paquetes\",\"packagename\":\"com.android.packageinstaller\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413547383148,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413548019067,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"com.lge.livewallpaper.prince\",\"appname\":\"unknown\",\"packagestatus\":\"REMOVED\",\"appversion\":\"- 1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413548020523,\"type\":\"uninstall\",\"properties\":{\"id\":\"3\",\"packagestatus\":\"REMOVED\",\"appversion\":\"- 1\",\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"unknown\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testConfidentialSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testConfidentialSecure1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556338789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547099406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413556194415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556306246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556345697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testConfidentialUnsecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"WEP\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	

private final String testConfidentialFileSensor = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"WEP\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/SWE\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"null\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testVirusCleaned = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_cleaned\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/aware_app_remote_files\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\",\"clean_type\":\"quarantine\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testEmailWithoutAttachmentsHashId = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 0,\"attachmentInfo\": \"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"id\":1976}";
	
	private final String testDatabaseObjects = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"0\",\"attachmentInfo\":\"\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1977}";
	
	private final String testVirusFoundHashId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_found\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/SWE\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1978}";
	
	private final String testEmailWithAttachmentsHashId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1979}";
	
	private final String testLogin = "{\"password\":\"muses\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"login\"}";
	
	private final String testLoginFailPass = "{\"password\":\"jsfdkjf\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"login\"}";
	
	private final String testLoginFailUser = "{\"password\":\"jsfdkjf\",\"device_id\":\"358648051980583\",\"username\":\"dfasdf\",\"requesttype\":\"login\"}";
	
	private final String testLoginSuccess = "{\"password\":\"pass\",\"device_id\":\"358648051980583\",\"username\":\"joe\",\"requesttype\":\"login\"}";
	
	private final String testLoginNotEnabled = "{\"password\":\"56836458345673465\",\"device_id\":\"358648051980583\",\"username\":\"notfound\",\"requesttype\":\"login\"}";
	
	//private final String testLoginNotEnabled = "{\"password\":\"swe_test\",\"device_id\":\"358648051980583\",\"username\":\"swe-tester-2\",\"requesttype\":\"login\"}";
	
	private final String testLogout = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"logout\"}";
	
	private final String testAntivirusNotRunning = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"100\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testIsRooted = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"100\",\"isrooted\":\"true\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"70\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"isrooted\":\"true\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testNotProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPatternProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"true\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"true\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPasswordProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"ispatternprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"ispatternprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPolicyOpenBlacklistGenericApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"uTorrent\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.utorrent\",\"appname\":\"uTorrent\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	//private final String testUnsafeStorage = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testUnsafeStorage = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backup;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.other.android.other\",\"appname\":\"Other\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testInstallNotAllowedApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	//private final String testInZone = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"true\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testInZone = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testZone1Restriction = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"open_application\",\"timestamp\":1428357713480,\"properties\":{\"packagename\":\"com.google.android.GoogleCamera\",\"appname\":\"Camara\",\"package\":\"\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testOpenFileInMonitoredFolderInRestrictedZone = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testEventForSessionId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galería,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Teléfono,com.android.phone,30241103;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Información de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento teléfono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;Búsqueda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;Síntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;Cámara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;Música,com.lge.music,32019;Configuración de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Corrección de la relación de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;Música,com.lge.sizechangable.musicwidget.widget,31013;Álbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;Vídeos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"1\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"id\":1976,\"requesttype\":\"local_decision\"}";
	
	private final String testOpenConfidentialAware = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1430827029134,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1430826989605,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"30\",\"isrooted\":\"false\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"10.122.77.183\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"mobileconnected\":\"true\",\"wifiencryption\":\"unknown\",\"timestamp\":1430827029741,\"bssid\":\"null\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"-1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1430826989619,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,2311111;Contactos,com.android.contacts,30902902;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galera,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;Battery Solo Widget,net.maicas.android.batterys,23;PassAndroid,org.ligi.passandroid,254;cryptonite,LGSmartcardService,org.simalliance.openmobileapi.service,3;Amazon,uk.amazon.mShop.android,502010\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1430827037341,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/aware_app_remote_files/MUSES_confidential_doc.pdf\",\"resourceName\":\"statistics\",\"resourceType\":\"CONFIDENTIAL\"}},\"id\":394997978,\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testUserEnteredPassword = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"user_entered_password_field\",\"timestamp\":1428357713480,\"properties\":{\"packagename\":\"com.google.android.gm\"}},\"requesttype\":\"online_decision\",\"device_id\":\"36474929437562939\",\"username\":\"muses\"}";
	
	private final String testUSBDeviceConnected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"usb_device_connected\",\"timestamp\":1428357713480,\"properties\":{\"connected_via_usb\":\"true\"}},\"requesttype\":\"online_decision\",\"device_id\":\"36474929437562939\",\"username\":\"muses\", \"id\":\"111\" }";

	private final String testAddEmasNote = "{\"sensor\":{},\"action\":{\"timestamp\":1433518371811,\"type\":\"add_note\",\"properties\":{\"id_event\":\"holala\",\"id_user\":\"1849\",\"description\":\"G\",\"title\":\"g\"}},\"id\":-775048452,\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testOpenFileInMonitoredFolderBluetooth = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testOpenFileInMonitoredFolderUnsecureWifi = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/confidential\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Búsqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicación MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electrónico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Teléfono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones búsqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Información de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuración de red móvil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador móvil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Búsqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicación de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuración para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuración,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronización de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronización de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronización de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Síntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducción de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Búsqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Cámara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresión móvil,com.sec.android.app.mobileprint,21;Reproductor de música,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de vídeo,com.sec.android.app.ve,4;Reproductor de vídeo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galería,com.sec.android.gallery3d,30682;Comando rápido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analógico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualización de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;Cryptonite,csh.cryptonite,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/SWE\\/confidential\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testSaveAssetReal = "{\"id\":-2127267967,\"requesttype\":\"local_decision\",\"device_id\":\"359521065844450\",\"username\":\"muses\",\"action\":{\"type\":\"save_asset\",\"timestamp\":1441797914882,\"properties\":{\"resourceType\":\"null\",\"resourceName\":\"test1.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"close_write\"}},\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1441797858061,\"packagename\":\"com.estrongs.android.pop\",\"backgroundprocess\":\"[com.sec.android.service.health]\",\"appname\":\"ES File Explorer\",\"appversion\":\"1506061237\",\"id\":\"3\"},\"CONTEXT_SENSOR_FILEOBSERVER\":{\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"timestamp\":1441797914882,\"resourceType\":\"null\",\"resourceName\":\"test1.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_PACKAGE\":{\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1441797818216,\"packagename\":\"\",\"packagestatus\":\"\",\"appname\":\"\",\"appversion\":\"-1\",\"installedapps\":\"Beaming Service,com.mobeam.barcodeService,19,com.samsung.location,1\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"timestamp\":1441797818370,\"airplanemode\":\"false\",\"wifiencryption\":\"[WPS][ESS]\",\"bssid\":\"06:1c:f0:f1:b1:08\",\"wifineighbors\":\"13\",\"wificonnected\":\"true\",\"networkid\":\"24\",\"mobileconnected\":\"false\",\"wifienabled\":\"true\",\"bluetoothconnected\":\"FALSE\",\"hiddenssid\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"timestamp\":1441797829389,\"screentimeoutinseconds\":\"15\",\"musesdatabasecontainscfg\":\"true\",\"ispasswordprotected\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"accessibilityenabled\":\"true\",\"musesdatabaseexists\":\"true\",\"ipaddress\":\"192.168.0.199\",\"isrooted\":\"false\"}}}";
	
	private final String testMaybeNormal = "{\"id\":-307153249,\"requesttype\":\"online_decision\",\"device_id\":\"359521065844450\",\"username\":\"muses\",\"action\":{\"type\":\"open_asset\",\"timestamp\":1441954931265,\"properties\":{\"resourceType\":\"null\",\"resourceName\":\"maybe_file.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"open\"}},\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1441954918719,\"packagename\":\"com.estrongs.android.pop\",\"backgroundprocess\":\"[com.sec.android.app.soundalive]\",\"appname\":\"ES File Explorer\",\"appversion\":\"1506061237\",\"id\":\"3\"},\"CONTEXT_SENSOR_FILEOBSERVER\":{\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"timestamp\":1441954931265,\"resourceType\":\"null\",\"resourceName\":\"maybe_file.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"open\"},\"CONTEXT_SENSOR_PACKAGE\":{\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1441954899340,\"packagename\":\"\",\"packagestatus\":\"\",\"appname\":\"\",\"appversion\":\"-1\",\"installedapps\":\"Beaming Service,com.mobeam.barcodeService,19;FilterProvider,com.samsung.android.provider.filterprovider,8;RoseEUKor,com.monotype.android.font.rosemary,1;Automation Test,com.sec.android.app.DataCreate,1;Mapcon Provider,com.sec.android.providers.mapcon,22;Skype,com.samsung.android.coreapps,115052201;ESPNcricinfo,com.july.cricinfo,47;Video,com.samsung.android.video,1505261425;Email,com.samsung.android.email.widget,110539534;SLLibrary,com.samsung.location,1\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"timestamp\":1441954909563,\"airplanemode\":\"false\",\"wifiencryption\":\"[WPS][ESS]\",\"bssid\":\"06:1c:f0:f1:b1:08\",\"wifineighbors\":\"21\",\"wificonnected\":\"true\",\"networkid\":\"28\",\"mobileconnected\":\"false\",\"wifienabled\":\"true\",\"bluetoothconnected\":\"FALSE\",\"hiddenssid\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"timestamp\":1441954910399,\"screentimeoutinseconds\":\"10\",\"musesdatabasecontainscfg\":\"true\",\"ispasswordprotected\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"accessibilityenabled\":\"true\",\"musesdatabaseexists\":\"true\",\"ipaddress\":\"192.168.0.199\",\"isrooted\":\"false\"}}}";
	
	private final String testMaybeOpportunity = "{\"id\":-307153249,\"requesttype\":\"online_decision\",\"device_id\":\"359521065844450\",\"username\":\"muses\",\"action\":{\"type\":\"open_asset\",\"timestamp\":1441954931265,\"properties\":{\"resourceType\":\"null\",\"resourceName\":\"opportunity.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"open\"}},\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1441954918719,\"packagename\":\"com.estrongs.android.pop\",\"backgroundprocess\":\"[com.sec.android.app.soundalive]\",\"appname\":\"ES File Explorer\",\"appversion\":\"1506061237\",\"id\":\"3\"},\"CONTEXT_SENSOR_FILEOBSERVER\":{\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"timestamp\":1441954931265,\"resourceType\":\"null\",\"resourceName\":\"opportunity.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"open\"},\"CONTEXT_SENSOR_PACKAGE\":{\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1441954899340,\"packagename\":\"\",\"packagestatus\":\"\",\"appname\":\"\",\"appversion\":\"-1\",\"installedapps\":\"Beaming Service,com.mobeam.barcodeService,19;FilterProvider,com.samsung.android.provider.filterprovider,8;RoseEUKor,com.monotype.android.font.rosemary,1;Automation Test,com.sec.android.app.DataCreate,1;Mapcon Provider,com.sec.android.providers.mapcon,22;Skype,com.samsung.android.coreapps,115052201;ESPNcricinfo,com.july.cricinfo,47;Video,com.samsung.android.video,1505261425;Email,com.samsung.android.email.widget,110539534;SLLibrary,com.samsung.location,1\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"timestamp\":1441954909563,\"airplanemode\":\"false\",\"wifiencryption\":\"[WPS][ESS]\",\"bssid\":\"06:1c:f0:f1:b1:08\",\"wifineighbors\":\"21\",\"wificonnected\":\"true\",\"networkid\":\"28\",\"mobileconnected\":\"false\",\"wifienabled\":\"true\",\"bluetoothconnected\":\"FALSE\",\"hiddenssid\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"timestamp\":1441954910399,\"screentimeoutinseconds\":\"10\",\"musesdatabasecontainscfg\":\"true\",\"ispasswordprotected\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"accessibilityenabled\":\"true\",\"musesdatabaseexists\":\"true\",\"ipaddress\":\"192.168.0.199\",\"isrooted\":\"false\"}}}";
	
	
	private final String testUptoYou = "{\"id\":307414627,\"requesttype\":\"online_decision\",\"device_id\":\"359521065844450\",\"username\":\"muses\",\"action\":{\"type\":\"save_asset\",\"timestamp\":1441954941695,\"properties\":{\"resourceType\":\"null\",\"resourceName\":\"up_to_you_file.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"close_write\"}},\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1441954918719,\"packagename\":\"com.estrongs.android.pop\",\"backgroundprocess\":\"[com.sec.android.app.soundalive]\",\"appname\":\"ES File Explorer\",\"appversion\":\"1506061237\",\"id\":\"3\"},\"CONTEXT_SENSOR_FILEOBSERVER\":{\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"timestamp\":1441954941695,\"resourceType\":\"null\",\"resourceName\":\"up_to_you_file.txt\",\"path\":\"\\/storage\\/emulated\\/0\\/SWE\\/Confidential\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_PACKAGE\":{\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1441954899340,\"packagename\":\"\",\"packagestatus\":\"\",\"appname\":\"\",\"appversion\":\"-1\",\"installedapps\":\"Beaming Service,com.mobeam.barcodeService,19;FilterProvider,com.samsung.android.provider.filterprovider,8;RoseEUKor,com.monotype.android.font.rosemary,1;Automation Test,com.sec.android.app.DataCreate,1;Mapcon Provider,com.sec.android.providers.mapcon,22;SLLibrary,Cryptonite, sh.cryptonite, com.samsung.location,1\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"timestamp\":1441954909563,\"airplanemode\":\"false\",\"wifiencryption\":\"[WPS][ESS]\",\"bssid\":\"06:1c:f0:f1:b1:08\",\"wifineighbors\":\"21\",\"wificonnected\":\"true\",\"networkid\":\"28\",\"mobileconnected\":\"false\",\"wifienabled\":\"true\",\"bluetoothconnected\":\"FALSE\",\"hiddenssid\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"timestamp\":1441954910399,\"screentimeoutinseconds\":\"10\",\"musesdatabasecontainscfg\":\"true\",\"ispasswordprotected\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"accessibilityenabled\":\"true\",\"musesdatabaseexists\":\"true\",\"ipaddress\":\"192.168.0.199\",\"isrooted\":\"false\"}}}";
	
	public final void testCluesDeviceStateSecurityIncident(){
		/*EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testFullCycleWithClues, "online_decision");
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testSecurityDeviceStateStep1, "online_decision");
		List<ContextEvent> list2 = JSONManager.processJSONMessage(testSecurityDeviceStateStep2, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		for (Iterator iterator = list2.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}*/
	}
	
	public final void testSecurityIncident(){
		
		User user = new User();
		UserTrustValue value = new UserTrustValue();
		value.setValue(1);
		user.setUsertrustvalue(value);
		
		SecurityIncident securityIncident = new SecurityIncident();
		securityIncident.setAssetid(1);
		securityIncident.setCostBenefit(1); //Included in the UI
		securityIncident.setDecisionid(0);
		securityIncident.setDescription("");
		securityIncident.setProbability(0.5);
		securityIncident.setUser(user);
		Probability probability = new Probability();
		probability.setValue(0.5);
		
		
		//notifySecurityIncident(probability, securityIncident);
		assertNotNull(user.getUsertrustvalue());
		
	}
	

	
	
	public final void testUserAction(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUserAction, "user_behavior");
		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		
	}
	
	public final void testPolicyOpenConfAssetSecure(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenConfAssetSecureReal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenConfAssetSecureReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
public final void testPolicyOpenConfAssetInSecure(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenConfAssetInSecure, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenConfAssetInSecure);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyOpenBlacklistApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		//List<ContextEvent> list = JSONManager.processJSONMessage(testBlacklistApp, "online_decision");
		List<ContextEvent> list = JSONManager.processJSONMessage(testBlacklistAppWifiAnalyzer, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				//root = new JSONObject(testBlacklistApp);
				root = new JSONObject(testBlacklistAppWifiAnalyzer);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testPolicyOpenNotBlacklistApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testNotBlacklistApp, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testNotBlacklistApp);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}

	public final void testPolicyEmailWithoutAttachments(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithoutAttachments, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testEmailWithoutAttachments);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyEmailWithAttachmentsVirusFound(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachments, "online_decision");
		//List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachmentsReal1, "online_decision");
		//List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFound, "online_decision");
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFoundReal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);

			JSONObject root;
			try {
				root = new JSONObject(testVirusFound);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			des.insertFact(formattedEvent);
		}
		

		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			
			JSONObject root;
			try {
				root = new JSONObject(testEmailWithAttachments);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		

	}
	
	
	//Features for prototype 1
	
	public final void testPolicyScreenLockDisable(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		//List<ContextEvent> list = JSONManager.processJSONMessage(testScreenLockDisableInAction, "online_decision");
		List<ContextEvent> list = JSONManager.processJSONMessage(testScreenLockDisableReal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				//root = new JSONObject(testScreenLockDisableInAction);
				root = new JSONObject(testScreenLockDisableReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	
	public final void testScreenLockTimeout(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testScreenLockTimeoutReal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testScreenLockTimeoutReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyACLForDemoUC6_user_muses_door1(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenAssetUC6_user_muses_door1, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenAssetUC6_user_muses_door1);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyACLForDemoUC6_user_muses2_door1(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenAssetUC6_user_muses2_door1, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenAssetUC6_user_muses2_door1);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyACLForDemoUC6_user_muses3_door1(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenAssetUC6_user_muses3_door1, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenAssetUC6_user_muses3_door1);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testUninstallRequiredApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUninstallRequiredApp, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUninstallRequiredApp);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	

	
	public final void testOpenFileInMonitoredFolder(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenFileInMonitoredFolder, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testOpenFileInMonitoredFolder);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testUninstall(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUninstall, null, defaultSessionId);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUninstall);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testConfidentialSecure(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testConfidentialSecure, null, defaultSessionId);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testConfidentialSecure);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testConfidentialUnsecure(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testConfidentialUnsecure, null, defaultSessionId);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testConfidentialUnsecure);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testRepeatedEvents(){
		
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testConfidentialSecure, null, defaultSessionId);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (int i = 0; i < 2; i++) {
			
			
			for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
				ContextEvent contextEvent = (ContextEvent) iterator.next();
				assertNotNull(contextEvent);
				Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
				JSONObject root;
				try {
				
					root = new JSONObject(testConfidentialSecure);
					formattedEvent.setSessionId(defaultSessionId);
					formattedEvent.setUsername(root
							.getString(JSONIdentifiers.AUTH_USERNAME));
					formattedEvent.setDeviceId(root
							.getString(JSONIdentifiers.AUTH_DEVICE_ID));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				formattedEvent.setSessionId(defaultSessionId);
				des.insertFact(formattedEvent);
			}
		}
			
			List<ContextEvent> list1 = JSONManager.processJSONMessage(testConfidentialSecure1, null, defaultSessionId);
		
			for (Iterator<ContextEvent> iterator1 = list1.iterator(); iterator1.hasNext();) {
				ContextEvent contextEvent = (ContextEvent) iterator1.next();
				assertNotNull(contextEvent);
				Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
				JSONObject root;
				try {
				
					root = new JSONObject(testConfidentialSecure);
					formattedEvent.setSessionId(defaultSessionId);
					formattedEvent.setUsername(root
							.getString(JSONIdentifiers.AUTH_USERNAME));
					formattedEvent.setDeviceId(root
							.getString(JSONIdentifiers.AUTH_DEVICE_ID));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				formattedEvent.setSessionId(defaultSessionId);
				des.insertFact(formattedEvent);
			}
		

		
	}
	
	public final void testPolicyEmailWithAttachmentsVirusFoundAndCleaned(){
		
		/*EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachments, "online_decision");
		//List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachmentsReal1, "online_decision");
		//List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFound, "online_decision");
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFoundReal, "online_decision");
		List<ContextEvent> list2 = JSONManager.processJSONMessage(testVirusCleaned, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);

			JSONObject root;
			try {
				root = new JSONObject(testVirusFoundReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			des.insertFact(formattedEvent);
		}
		
		//DeviceSecurityState changes due to virus found in the same device
		//testSecurityDeviceStateChange();
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			
			JSONObject root;
			try {
				root = new JSONObject(testEmailWithAttachments);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		
		for (Iterator<ContextEvent> iterator = list2.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			
			JSONObject root;
			try {
				root = new JSONObject(testVirusCleaned);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}*/
	}
	
	public final void testVirusCleaned(){
		/*EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFoundReal, "online_decision");
		List<ContextEvent> list2 = JSONManager.processJSONMessage(testVirusCleaned, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
	
		for (Iterator<ContextEvent> iterator = list1.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);

			JSONObject root;
			try {
				root = new JSONObject(testVirusFoundReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			des.insertFact(formattedEvent);
		}
		for (Iterator<ContextEvent> iterator = list2.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			
			JSONObject root;
			try {
				root = new JSONObject(testVirusCleaned);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}*/
	}
	
	public final void testPolicyEmailWithoutAttachmentsHashId(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithoutAttachmentsHashId, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testEmailWithoutAttachmentsHashId);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
				formattedEvent.setHashId(root
						.getInt(JSONIdentifiers.REQUEST_IDENTIFIER));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testDatabaseObjects(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testDatabaseObjects, "online_decision");		
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testDatabaseObjects);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
				formattedEvent.setHashId(root
						.getInt(JSONIdentifiers.REQUEST_IDENTIFIER));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testDatabaseObjectsWithViolation(){
		
		/*EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testEmailWithAttachmentsHashId, "online_decision");
		List<ContextEvent> list1 = JSONManager.processJSONMessage(testVirusFoundHashId, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		JSONObject root;
		try {
			root = new JSONObject(testVirusFoundHashId);
			String username =root
					.getString(JSONIdentifiers.AUTH_USERNAME);
			String deviceId =root
					.getString(JSONIdentifiers.AUTH_DEVICE_ID);
	
			UserContextEventDataReceiver.getInstance().processContextEventList(
				list, defaultSessionId, username, deviceId, 1979 );
			
			UserContextEventDataReceiver.getInstance().processContextEventList(
					list1, defaultSessionId, username, deviceId, 1978);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
	}
	
	public final void testLoginLogout(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testLogout);

	}
	
	public final void testLoginFailPass(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLoginFailPass);


	}
	
	public final void testLoginFailUser(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLoginFailUser);


	}
	
	public final void testLoginSuccess(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLoginSuccess);


	}
	
	public final void testLoginNotEnabled(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLoginNotEnabled);


	}
	
	public final void testRestart(){
		CorrelationStartupServlet servlet = new CorrelationStartupServlet();
		try {
			servlet.init();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final void testConfigSync(){
		final String testConfigSync = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"config_sync\"}";
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testConfigSync);


	}
	
	public final void testSecurityViolation(){
		final String testSecurityViolation = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 0,\"attachmentInfo\": \"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"id\":1976}";
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testSecurityViolation);


	}
	
	
	//New corporate policies
	
	public final void testAntivirusNotRunning(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testAntivirusNotRunning, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testAntivirusNotRunning);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testIsRooted(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testIsRooted, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testIsRooted);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testPatternProtected(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testPatternProtected, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testPatternProtected);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testNotProtected(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testNotProtected, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testNotProtected);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testPasswordProtected(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testPasswordProtected, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testPasswordProtected);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testPolicyOpenBlacklistGenericApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		//List<ContextEvent> list = JSONManager.processJSONMessage(testBlacklistApp, "online_decision");
		List<ContextEvent> list = JSONManager.processJSONMessage(testPolicyOpenBlacklistGenericApp, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				//root = new JSONObject(testBlacklistApp);
				root = new JSONObject(testPolicyOpenBlacklistGenericApp);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testUserBehavior(){
		final String testConfigSync = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"config_sync\"}";
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testUserAction3);


	}
	
	public final void testUserBehaviorReal(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUserAction3, "user_behavior");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUserAction3);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testUnsafeStorage(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUnsafeStorage, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUnsafeStorage);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testInstallNotAllowedApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testInstallNotAllowedApp, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testInstallNotAllowedApp);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testInZone(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testInZone, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testInZone);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	

	public final void testZone1Restriction(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testZone1Restriction, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testZone1Restriction);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testOpenFileInMonitoredFolderInRestrictedZone(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenFileInMonitoredFolderInRestrictedZone, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testOpenFileInMonitoredFolderInRestrictedZone);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	/**

	* testSecurityIncidentDetection: JUnit Test case whose aim is to check that a security incident is detected
	* @param none

	*/
	@Test
	public void testSecurityIncidentDetection() {
		
		
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;

		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testEventForSessionId);

		User user = new User();
		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(0);
		user.setUsertrustvalue(usertrustvalue);
		SecurityIncident securityIncident = new SecurityIncident();
		securityIncident.setCostBenefit(100000);
		
		
		DBManager dbManager= new DBManager(ModuleType.EP);		
		eu.musesproject.server.entity.Devices musesDevice = dbManager.getDeviceByIMEI("358648051980583");
		securityIncident.setDeviceid(Integer.valueOf(musesDevice.getDeviceId()));
		securityIncident.setAssetid(1);
		securityIncident.setDescription("Test Security Incident");
		securityIncident.setProbability(0.5);
		securityIncident.setUser(user);
		
		Probability probability = new Probability();
		probability.setValue(0.5);
		
		eu.musesproject.server.entity.SecurityIncident secIncident = new eu.musesproject.server.entity.SecurityIncident();
		secIncident.setDevice(musesDevice);
		secIncident.setName(securityIncident.getDescription());
		Users user1 = dbManager.getUserByUsername("muses");
		secIncident.setUser(user1);
		
		dbManager.setSecurityIncident(secIncident);
		
		
	}
	
	public final void testConfigSyncWithOS(){
		final String testConfigSync = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"operating-system-version\":\"5.0.2\",\"operating-system\":\"Android\",\"requesttype\":\"config_sync\"}";
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testConfigSync);


	}
	
	public final void testOpenConfidentialAware(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenConfidentialAware, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testOpenConfidentialAware);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testConfidentialFileSensor(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testConfidentialFileSensor, null, defaultSessionId);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testConfidentialFileSensor);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testOpenConfidentialAwareReal(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenConfidentialAware, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl callback = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		//callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testOpenConfidentialAware);
	}



	
	
	
	
	
	
	public final void testUserEnteredPassword(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUserEnteredPassword, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUserEnteredPassword);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testUSBDeviceConnected(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUSBDeviceConnected, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testUSBDeviceConnected);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testUSBDeviceConnectedInsertEvent(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl callback = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		//callback.receiveCb(defaultSessionId, testUSBDeviceConnected);
		
		
		
		List<ContextEvent> list = JSONManager
				.processJSONMessage(testUSBDeviceConnected, null, defaultSessionId);
		
		String username, deviceId= "";
		int requestId = 111;
		
		
		try {
			JSONObject root = new JSONObject(testUSBDeviceConnected);
			username = root
					.getString(JSONIdentifiers.AUTH_USERNAME);
			deviceId = root
					.getString(JSONIdentifiers.AUTH_DEVICE_ID);
			//if (requestType.equals(RequestType.ONLINE_DECISION)){
				requestId = root.getInt(JSONIdentifiers.REQUEST_IDENTIFIER);

			//}

		
			//UserContextEventDataReceiver.getInstance().processContextEventList(
				//list, defaultSessionId, username, deviceId, requestId);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	public final void testStorePoliciesOnStartup(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);

		}
	}	
	
	public final void testStoreRulesOnStartup(){
		
		EventProcessorImpl processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			processor.storeRulesOnStartup();
			assertNotNull(processor);

		}
	}
	
	public final void testAddEmasNote(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testAddEmasNote, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testAddEmasNote);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testOpenFileInMonitoredFolderUnsecureWifi(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenFileInMonitoredFolderUnsecureWifi, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenFileInMonitoredFolderUnsecureWifi);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
		
		final String testOpportunity = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"decision_id\":\"1549\",\"opportunity_working_hours\":\"15\",\"opportunity_revenue_loss_euros\":\"13\",\"opportunity_revenue_loss_description\":\"Can't use the pc right now\",\"requesttype\":\"opportunity-request\"}";
		List<ContextEvent> listOpportunity = JSONManager.processJSONMessage(testOpportunity, "opportunity-request");
		for (Iterator<ContextEvent> iterator = listOpportunity.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testOpportunity);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testOpenFileInMonitoredFolderBluetooth(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testOpenFileInMonitoredFolderBluetooth, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testOpenFileInMonitoredFolderBluetooth);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testSaveAssetReal(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testSaveAssetReal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testSaveAssetReal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testMaybeNormal(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testMaybeNormal, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testMaybeNormal);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testMaybeOpportunity(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testMaybeOpportunity, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testMaybeOpportunity);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testUptoyou(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testUptoYou, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
				root = new JSONObject(testUptoYou);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testWindowsUnsafeStorage(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		final String testConfigSyncWindows = "{\"requesttype\":\"config_sync\",\"device_id\":\"C0-18-85-C5-35-8B\",\"operating-system-version\":\"Windows 7\",\"operating-system\":\"Windows\",\"username\":\"muses\"}";
		String testWindows = "{\"requesttype\":\"online_decision\",\"device_id\":\"C0-18-85-C5-35-8B\",\"action\":{\"type\":\"open_application\",\"properties\":{\"appname\":\"Idle\",\"packagename\":\"Idle\",\"version\":\"null\"},\"timestamp\":1444223246808},\"sensor\":{\"CONTEXT_SENSOR_EMAIL\":{\"cc\":\"cc_1@a.com; cc_2@b.com\",\"bcc\":\"bcc_1@a.com; bcc_2@b.com\",\"attachments\":\"c:\\tmp\\test.txt; d:\\t.pdf\",\"receivers\":\"r_1@a.com; r_2@b.com\",\"subject\":\"test\",\"type\":\"CONTEXT_SENSOR_EMAIL\",\"timestamp\":1444223246765},\"CONTEXT_SENSOR_FILE_ACCESS\":{\"cancreate\":\"true\",\"canread\":\"false\",\"candelete\":\"false\",\"canexecute\":\"false\",\"canmodify\":\"false\",\"type\":\"CONTEXT_SENSOR_FILE_ACCESS\",\"timestamp\":1444223246765},\"CONTEXT_SENSOR_APP\":{\"backgroundprocess\":\"[]\",\"appname\":\"Idle\",\"packagename\":\"Idle\",\"appversion\":\"null\",\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1444223246765},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"istrustedantivirusinstalled\":\"false\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"ispasswordprotected\":\"true\",\"timestamp\":1444223246765,\"isscreanlocked\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"installedapps\":\"Adobe Flash Player 19 NPAPI,19.0.0.185;Git version 1.8.4-preview20130916,1.8.4-preview20130916;Google Chrome,45.0.2454.101;KeyStore Explorer 5.1.1,5.1.1;Microsoft Help Viewer 2.0,2.0.50727;Mozilla Firefox 41.0.1 (x86 en-US),41.0.1;Mozilla Thunderbird 38.2.0 (x86 en-US),38.2.0;Mozilla Maintenance Service,41.0.1.5750; for Microsoft .NET Framework 4.5 (KB2840642),1;Security Update for Microsoft .NET Framework 4.5 (KB2840642v2),2;Security Update for Microsoft .NET Framework 4.5 (KB2861208),1;Security Update for Microsoft .NET Framework 4.5 (KB2894854v2),2;Security Update for Microsoft .NET Framework 4.5 (KB2898864),1;Security Update for Microsoft .NET Framework 4.5 (KB2901118),1;Security Update for Microsoft .NET Framework 4.5 (KB2931368),1;Security Update for Microsoft .NET Framework 4.5 (KB2972107),1;Security Update for Microsoft .NET Framework 4.5 (KB2972216),1;Security Update for Microsoft .NET Framework 4.5 (KB2978128),1;Security Update for Microsoft .NET Framework 4.5 (KB2979578v2),2;Security Update for Microsoft .NET Framework 4.5 (KB3023224),1;Security Update for Microsoft .NET Framework 4.5 (KB3035490),1;Security Update for Microsoft .NET Framework 4.5 (KB3037581),1;Security Update for Microsoft .NET Framework 4.5 (KB3074230),1;Security Update for Microsoft .NET Framework 4.5 (KB3074550),1;Microsoft Office Outlook Connector,14.0.5118.5000;MySQL Examples and Samples 5.6,5.6.21;Realtek USB 2.0 Card Reader,6.1.7601.39019;MySQL Connector J,5.1.33;MySQL Connector/C 6.1,6.1.5;Visual Studio 2012 x86 Redistributables,14.0.0.1;CCC Help Finnish,2012.0305.0347.6610;MySQL Documents 5.6,5.6.21;Windows Live Writer,16.4.3528.0331;CCC Help Japanese,2012.0305.0347.6610;Microsoft Visual C++ 2005 Redistributable,8.0.50727.42;Google Update Helper,1.3.25.11;CCC Help Korean,2012.0305.0347.6610;Adobe Refresh Manager,1.8.0;Adobe Reader XI (11.0.12),11.0.12;Microsoft Visual C++ 2012 x86 Additional Runtime - 11.0.61030,11.0.61030;Windows Live PIMT Platform,16.4.3528.0331;Windows Live Mail,16.4.3528.0331;CCC Help Chinese Standard,2012.0305.0347.6610;CCC Help French,2012.0305.0347.6610;Windows Live Mail,16.4.3528.0331;Windows Live Messenger,16.4.3528.0331;Catalyst Control Center Profiles Mobile,2012.0305.348.6610;Microsoft Visual C++ 2012 x86 Minimum Runtime - 11.0.61030,11.0.61030;CCC Help German,2012.0305.0347.6610;CCC Help Danish,2012.0305.0347.6610;CCC Help Italian,2012.0305.0347.6610;ComProbe Protocol Analysis System,1.00.0000;Muses.WindowsClient.Service.Installer,1.0.0;Photo Gallery,16.4.3528.0331;Microsoft Visual C++ 2012 Redistributable (x64) - 11.0.61030,11.0.61030.0;Photo Common,16.4.3528.0331;MySQL Notifier 1.1.6,1.1.6;Windows Live SOXE,16.4.3528.0331;Catalyst Control Center - Branding,1.00.0000;Microsoft .NET Framework 4 Multi-Targeting Pack,4.0.30319;Update for  (KB2504637),1;MSVCRT_amd64,15.4.2862.0708;Windows Live SOXE Definitions,16.4.3528.0331;Catalyst Control Center Localization All,2012.0305.348.6610;CCC Help Portuguese,2012.0305.0347.6610;Microsoft SQL Server 2012 Management Objects ,11.0.2100.60;CCC Help Spanish,2012.0305.0347.6610;Movie Maker,16.4.3528.0331;MySQL Connector C++ 1.1.4,1.1.4;D3DX10,15.4.2368.0902;CCC Help Chinese Traditional,2012.0305.0347.6610;Microsoft System CLR Types for SQL Server 2012,11.0.2100.60;Windows Live Messenger,16.4.3528.0331;Cisco PEAP Module,1.1.6;Microsoft SQL Server 2005 Compact Edition [ENU],3.1.0000;Microsoft Visual C++ 2010  x86 Redistributable - 10.0.40219,10.0.40219;Intel(R) Display Audio Driver,6.14.00.3090;MSXML 4.0 SP2 (KB973688),4.20.9876.0;Microsoft SQL Server Data Tools Build Utilities - enu (11.1.20828.01),11.1.20828.01;Microsoft SQL Server 2012 1;MySQL Connector Net 6.9.4,6.9.4;Apple Application Support (32-bit),3.2;CCC Help Russian,2012.0305.0347.6610;Google Earth,7.1.5.1557;Microsoft Visual C++ 2005 Redistributable,8.0.59193;MSXML 4.0 SP2 (KB954430),4.20.9870.0;Realtek Ethernet Controller Driver,7.54.309.2012;MySQL Installer - Community,1.4.2.0;MSVCRT,15.4.2862.0708;MSVCRT110,16.4.1108.0727;Microsoft Office Excel MUI (English) 2007,12.0.6612.1000;Microsoft Office PowerPoint MUI (English) 2007,12.0.6612.1000;Microsoft Office Outlook MUI (English) 2007,12.0.6612.1000;Microsoft Office Word MUI (English) 2007,12.0.6612.1000;Microsoft Office Proof (English) 2007,12.0.6612.1000;Microsoft Office Proof (French) 2007,12.0.6612.1000;Microsoft Office Proof (Spanish) 2007,12.0.6612.1000;Microsoft Office Proofing (English) 2007,12.0.4518.1014;Microsoft Office Shared MUI (English) 2007,12.0.6612.1000;Microsoft Office Shared Setup Metadata MUI (English) 2007,12.0.6612.1000;Microsoft Office File Validation Add-In,14.0.5130.5003;Microsoft Office Standard 2007,12.0.6612.1000;Prerequisites for SSDT ,11.0.2100.60;Security Update for Microsoft .NET Framework 4.5 (KB2737083),1;Security Update for Microsoft .NET Framework 4.5 (KB2742613),1;Security Update for Microsoft .NET Framework 4.5 (KB2789648),1;Security Update for Microsoft .NET Framework 4.5 (KB2804582),1;Security Update for Microsoft .NET Framework 4.5 (KB2833957),1;Security UpdateWriter,16.4.3528.0331;SmartSVN 7.5,7.1.10;Photo Gallery,16.4.3528.0331;MySQL Utilities,1.4.4;Junk Mail filter update,16.4.3528.0331;Catalyst Control Center,2012.0305.348.6610;Windows Live Writer Resources,16.4.3528.0331;PX Profile Update,1.00.1.;Microsoft .NET Framework 4.5 SDK,4.5.50709;Windows Live Photo Common,16.4.3528.0331;Intel(R) USB 3.0 eXtensible Host Controller Driver,1.0.3.214;Catalyst Control Center InstallProxy,2012.0305.348.6610;CCC Help English,2012.0305.0347.6610;Entity Framework Designer for Visual Studio 2012 - enu,11.1.20810.00;Microsoft Visual C++ 2012 Redistributable (x86) - 11.0.61030,11.0.61030.0;Movie Maker,16.4.3528.0331;Intel(R) Rapid Storage Technology,11.1.0.1006;CCC Help Norwegian,2012.0305.0347.6610;Windows Live Communications Platform,16.4.3528.0331;Java Auto Updater,2.8.60.27;MySQL Connector/ODBC 5.3,5.3.4;HTC Driver Installer,4.10.0.001;CCC Help Swedish,2012.0305.0347.6610;Microsoft SQL Server Data Tools - enu (11.1.20828.01),11.1.20828.01;Cisco LEAP Module,1.0.19;PowerXpressHybrid,1.00.0000;CCC Help Dutch,2012.0305.0347.6610;Microsoft .NET Framework 4.5 Multi-Targeting Pack,4.5.50709;Google Update Helper,1.3.28.15;Windows Live Family Safety,16.4.3528.0331;Cisco EAP-FAST Module,2.2.14;Intel(R) Management Engine Components,8.0.1.1399;Windows Live UX Platform Language Pack,16.4.3528.0331;Windows Live Installer,16.4.3528.0331;Windows Live Essentials,16.4.3528.0331;Skypeâ\u0084¢ 7.8,7.8.102;Skype Click to Call,7.4.0.9058;Microsoft SQL Server 2012 T-SQL Language Service ,11.0.2100.60;Microsoft Visual C++ 2005 Redistributable,8.0.61001;Windows Live Writer,16.4.3528.0331;Microsoft Visual C++ 2013 Redistributable (x64) - 12.0.21005,12.0.21005.Data-Tier App Framework ,11.0.2316.0;Microsoft Help Viewer 2.0,2.0.50727\",\"appname\":\"init\",\"packagestatus\":\"unknown\",\"appversion\":\"init\",\"id\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1444223246765}},\"id\":-1048923444,\"username\":\"muses\"}";
		
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testConfigSyncWindows);
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testWindows, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testWindows);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);
		}
	}
	
	public final void testConfigSyncWithWindowsOS(){
		final String testConfigSyncWindows = "{\"requesttype\":\"config_sync\",\"device_id\":\"C0-18-85-C5-35-8B\",\"operating-system-version\":\"Windows 7\",\"operating-system\":\"Windows\",\"username\":\"muses\"}";
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		//ConnectionCallbacksImpl cb = new ConnectionCallbacksImpl();
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testLogin);
		callback.receiveCb(defaultSessionId, testConfigSyncWindows);


	}
	
	public final void testWindowsBlacklist(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		final String testConfigSyncWindows = "{\"requesttype\":\"config_sync\",\"device_id\":\"C0-18-85-C5-35-8B\",\"operating-system-version\":\"Windows 7\",\"operating-system\":\"Windows\",\"username\":\"muses\"}";
		String testWindows = " {\"requesttype\":\"online_decision\",\"device_id\":\"D4-BE-D9-46-CF-FA\",\"action\":{\"type\":\"open_application\",\"properties\":{\"appname\":\"Dropbox Setup\",\"packagename\":\"Dropbox Setup\",\"version\":\"0.0\"},\"timestamp\":1444230989465},\"sensor\":{\"CONTEXT_SENSOR_EMAIL\":{\"cc\":\"cc_1@a.com; cc_2@b.com\",\"bcc\":\"bcc_1@a.com; bcc_2@b.com\",\"attachments\":\"c:\\tmp\\test.txt; d:\\t.pdf\",\"receivers\":\"r_1@a.com; r_2@b.com\",\"subject\":\"test\",\"type\":\"CONTEXT_SENSOR_EMAIL\",\"timestamp\":1444230594161},\"CONTEXT_SENSOR_FILE_ACCESS\":{\"cancreate\":\"true\",\"canread\":\"false\",\"candelete\":\"false\",\"canexecute\":\"false\",\"canmodify\":\"false\",\"type\":\"CONTEXT_SENSOR_FILE_ACCESS\",\"timestamp\":1444230594154},\"CONTEXT_SENSOR_APP\":{\"backgroundprocess\":\"[]\",\"appname\":\"Dropbox Setup\",\"packagename\":\"Dropbox Setup\",\"appversion\":\"0.0\",\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1444230989465},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"ipaddress\":\"192.168.1.100\",\"bssid\":\"f81a67837158,54e6fcd170de,24a43c17e640,f029294d2590,e01c41d359d4,c0ffd480a1fd,0a18d69d230b,e01c41d359d5,b4b52f15e4af,54e6fcd170de\",\"connectedtotrustediprange\":\"false\",\"wificonnected\":\"true\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"deviceconnected\":\"true\",\"wifienabled\":\"true\",\"ethernetconnected\":\"true\",\"networkname\":\"MUSES_UNSEC\",\"wifiencryption\":\"none\",\"networkid\":\"{7CE1A057-5777-4FC9-95CE-089FCAE68D6D}\",\"id\":\"3\",\"timestamp\":1444230616147},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"isscreenlocked\":\"null\",\"istrustedantivirusinstalled\":\"false\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"ispasswordprotected\":\"true\",\"timestamp\":1444230594154},\"CONTEXT_SENSOR_PACKAGE\":{\"installedapps\":\"Adobe Flash Player 19 NPAPI,19.0.0.185;Avast Premier,10.4.2233;Dropbox,3.10.7;Git version 1.8.4-preview20130916,1.8.4-preview20130916;Google Chrome,45.0.2454.101;KeyStore Explorer 5.1.1,5.1.1;Microsoft Help Viewer 2.0,2.0.50727;Mozilla Firefox 41.0.1 (x86 en-US),41.0.1;Mozilla Thunderbird 38.2.0 (x86 en-US),0305.0347.6610;CCC Help Italian,2012.0305.0347.6610;ComProbe Protocol Analysis System,1.00.0000;Muses.WindowsClient.Service.Installer,1.0.0;Photo Gallery,16.4.3528.0331;Microsoft Visual C++ 2012 Redistributable (x64) - 11.0.61030,11.0.61030.0;Photo Common,16.4.3528.0331;MySQL Notifier 1.1.6,1.1.6;Windows Live SOXE,16.4.3528.0331;Catalyst Control Center - Branding,1.00.0000;Microsoft .NET Framework 4 Multi-Targeting Pack,4.0.30319;Update for  (KB2504637),1;MSVCRT_amd64,15.4.2862.0708;Windows Live SOXE Definitions,16.4.3528.0331;Catalyst Control Center Localization All,2012.0305.348.6610;CCC Help Portuguese,TrueCrypt,2012.0305.0347.6610;Microsoft SQL Server 2012 Management Objects ,11.0.2100.60;CCC Help Spanish,2012.0305.0347.6610;Movie Maker,16.4.3528.0331;MySQL Connector C++ 1.1.4,1.1.4;D3DX10,15.4.2368.0902;CCC Help Chinese Traditional,2012.0305.0347.6610;Microsoft System CLR Types for SQL Server 2012,11.0.2100.60;Windows Live Messenger,16.4.3528.0331;Cisco PEAP Module,1.1.6;Microsoft SQL Server 2005 Compact Edition [ENU],3.1.0000;Microsoft Visual C++ 2010  x86 Redistributable - 10.0.40219,10.0.40219;Intel(R) Display Audio Driver,6.14.00.3090;MSXML 4.0 SP2 (KB973688),4.20.9876.0;Microsoft SQL Server Data Tools Build Utilities - enu (11.1.20828.01),11.1.20828.01;Microsoft SQL Server 2012 Data-Tier App Framework ,11.0.2316.0;Microsoft Help Viewer 2.0,2.0.50727\",\"appname\":\"init\",\"packagestatus\":\"unknown\",\"appversion\":\"init\",\"id\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1444230594154}},\"id\":2011783024,\"username\":\"muses\"}";
		
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testConfigSyncWindows);
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testWindows, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testWindows);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	public final void testWindowsUninstallRequiredApp(){
		
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		final String testConfigSyncWindows = "{\"requesttype\":\"config_sync\",\"device_id\":\"C0-18-85-C5-35-8B\",\"operating-system-version\":\"Windows 7\",\"operating-system\":\"Windows\",\"username\":\"muses\"}";
		String testWindows = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
		
		
		
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(defaultSessionId, testConfigSyncWindows);
		
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testUninstallRequiredApp, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testWindows);
				formattedEvent.setSessionId(defaultSessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(defaultSessionId);
			//des.insertFact(formattedEvent);
		}
	}
	
	
	public final void testBugDropboxBlacklist(){
		
		/*EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		
		final String testConfigSyncWindows = "{\"requesttype\":\"config_sync\",\"device_id\":\"D4-BE-D9-46-CF-FA\",\"operating-system-version\":\"Windows 7\",\"operating-system\":\"Windows\",\"username\":\"muses\"}";	
		//String testWindows = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
		String testWindows = "{\"requesttype\":\"online_decision\",\"device_id\":\"D4-BE-D9-46-CF-FA\",\"action\":{\"type\":\"open_application\",\"properties\":{\"appname\":\"Dropbox Setup\",\"packagename\":\"Dropbox Setup\",\"version\":\"0.0\"},\"timestamp\":1444636174968},\"sensor\":{\"CONTEXT_SENSOR_EMAIL\":{\"cc\":\"cc_1@a.com; cc_2@b.com\",\"bcc\":\"bcc_1@a.com; bcc_2@b.com\",\"attachments\":\"c:\\tmp\\test.txt; d:\\t.pdf\",\"receivers\":\"r_1@a.com; r_2@b.com\",\"subject\":\"test\",\"type\":\"CONTEXT_SENSOR_EMAIL\",\"timestamp\":1444636112997},\"CONTEXT_SENSOR_FILE_ACCESS\":{\"cancreate\":\"true\",\"canread\":\"false\",\"candelete\":\"false\",\"canexecute\":\"false\",\"canmodify\":\"false\",\"type\":\"CONTEXT_SENSOR_FILE_ACCESS\",\"timestamp\":1444636112996},\"CONTEXT_SENSOR_APP\":{\"backgroundprocess\":\"[]\",\"appname\":\"Dropbox Setup\",\"packagename\":\"Dropbox Setup\",\"appversion\":\"0.0\",\"type\":\"CONTEXT_SENSOR_APP\",\"timestamp\":1444636174968},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"ipaddress\":\"192.168.1.100\",\"bssid\":\"f81a67837158,80b686bf5a3a,f029294d2591,f029294d2593,06180a7a3782,f029294d2590,f029294d2592,c0ffd480a1fd,0a18d69d230b,54e6fcd170de,24dec67abab1,000f618adb90,54e6fcd170de\",\"connectedtotrustediprange\":\"false\",\"wificonnected\":\"true\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"deviceconnected\":\"true\",\"wifienabled\":\"true\",\"ethernetconnected\":\"true\",\"networkname\":\"MUSES_UNSEC\",\"wifiencryption\":\"none\",\"networkid\":\"{7CE1A057-5777-4FC9-95CE-089FCAE68D6D}\",\"id\":\"3\",\"timestamp\":1444636134965},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"isscreenlocked\":\"null\",\"istrustedantivirusinstalled\":\"false\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\",\"ispasswordprotected\":\"true\",\"timestamp\":1444636112996},\"CONTEXT_SENSOR_PACKAGE\":{\"installedapps\":\"Adobe Flash Player 19 NPAPI,19.0.0.185;Avast Premier,10.4.2233;Dropbox,3.10.7;Git version 1.8.4-preview20130916,1.8.4-preview20130916;Google Chrome,45.0.2454.101;KeyStore Explorer 5.1.1,5.1.1;Microsoft Help Viewer 2.0,2.0.50727;Mozilla Firefox 41.0.1 (x86 en-US),41.0.1;Mozilla Thunderbird 38.2.0 (x86 en-US),38.2.0;Mozilla Maintenance Service,41.0.1.5750;Muses 1.1,1.1;Notepad++,6.5.4;Nullsoft Install System,3.0b1;Microsoft Office Standard 2007,12.0.6612.1000;Windows Live Essentials,16.4.3528.0331;WinPcap 4.1.3,4.1.0.2980;WinRAR 4.20 (32-bit),4.20.0;WinSCP 5.5.6,5.5.6;Windows Live UX Platform,16.4.3528.0331;Windows Live Writer,16.4.3528.0331;SmartSVN 7.5,7.1.10;Photo Gallery,16.4.3528.0331;Dropbox Update Helper,1.3.27.37;MySQL Utilities,1.4.4;Junk Mail filter update,16.4.3528.0331;Catalyst Control Center,2012.0305.348.6610;Windows Live Writer Resources,16.4.3528.0331;PX Profile Update,1.00.1.;Microsoft .NET Framework 4.5 SDK,4.5.50709;Windows Live Photo Common,16.4.3528.0331;Intel(R) USB 3.0 eXtensible Host Controller Driver,1.0.3.214;Catalyst Control Center InstallProxy,2012.0305.348.6610;CCC Help English,2012.0305.0347.6610;Entity Framework Designer for Visual Studio 2012 - enu,11.1.20810.00;Microsoft Visual C++ 2012 Redistributable (x86) - 11.0.61030,11.0.61030.0;Movie Maker,16.4.3528.0331;Intel(R) Rapid Storage Technology,11.1.0.1006;CCC Help Norwegian,2012.0305.0347.6610;Windows Live Communications Platform,16.4.3528.0331;Java Auto Updater,2.8.60.27;MySQL Connector/ODBC 5.3,5.3.4;HTC Driver Installer,4.10.0.001;CCC Help Swedish,2012.0305.0347.6610;Microsoft SQL Server Data Tools - enu (11.1.20828.01),11.1.20828.01;Cisco LEAP Module,1.0.19;PowerXpressHybrid,1.00.0000;CCC Help Dutch,2012.0305.0347.6610;Microsoft .NET Framework 4.5 Multi-Targeting Pack,4.5.50709;Google Update Helper,1.3.28.15;Windows Live Family Safety,16.4.3528.0331;Cisco EAP-FAST Module,2.2.14;Intel(R) Management Engine Components,8.0.1.1399;Windows Live UX Platform Language Pack,16.4.3528.0331;Windows Live Installer,16.4.3528.0331;Windows Live Essentials,16.4.3528.0331;Skype�\u0084� 7.8,7.8.102;Skype Click to Call,7.4.0.9058;Microsoft SQL Server 2012 T-SQL Language Service ,11.0.2100.60;Microsoft Visual C++ 2005 Redistributable,8.0.61001;Windows Live Writer,16.4.3528.0331;Microsoft Visual C++ 2013 Redistributable (x64) - 12.0.21005,12.0.21005.1;MySQL Connector Net 6.9.4,6.9.4;Apple Application Support (32-bit),3.2;CCC Help Russian,2012.0305.0347.6610;Google Earth,7.1.5.1557;Microsoft Visual C++ 2005 Redistributable,8.0.59193;MSXML 4.0 SP2 (KB954430),4.20.9870.0;Realtek Ethernet Controller Driver,7.54.309.2012;MySQL Installer - Community,1.4.2.0;MSVCRT,15.4.2862.0708;MSVCRT110,16.4.1108.0727;Microsoft Office Excel MUI (English) 2007,12.0.6612.1000;Microsoft Office PowerPoint MUI (English) 2007,12.0.6612.1000;Microsoft Office Outlook MUI (English) 2007,12.0.6612.1000;Microsoft Office Word MUI (English) 2007,12.0.6612.1000;Microsoft Office Proof (English) 2007,12.0.6612.1000;Microsoft Office Proof (French) 2007,12.0.6612.1000;Microsoft Office Proof (Spanish) 2007,12.0.6612.1000;Microsoft Office Proofing (English) 2007,12.0.4518.1014;Microsoft Office Shared MUI (English) 2007,12.0.6612.1000;Microsoft Office Shared Setup Metadata MUI (English) 2007,12.0.6612.1000;Microsoft Office File Validation Add-In,14.0.5130.5003;Microsoft Office Standard 2007,12.0.6612.1000;Prerequisites for SSDT ,11.0.2100.60;Security Update for Microsoft .NET Framework 4.5 (KB2737083),1;Security Update for Microsoft .NET Framework 4.5 (KB2742613),1;Security Update for Microsoft .NET Framework 4.5 (KB2789648),1;Security Update for Microsoft .NET Framework 4.5 (KB2804582),1;Security Update for Microsoft .NET Framework 4.5 (KB2833957),1;Security Update for Microsoft .NET Framework 4.5 (KB2840642),1;Security Update for Microsoft .NET Framework 4.5 (KB2840642v2),2;Security Update for Microsoft .NET Framework 4.5 (KB2861208),1;Security Update for Microsoft .NET Framework 4.5 (KB2894854v2),2;Security Update for Microsoft .NET Framework 4.5 (KB2898864),1;Security Update for Microsoft .NET Framework 4.5 (KB2901118),1;Security Update for Microsoft .NET Framework 4.5 (KB2931368),1;Security Update for Microsoft .NET Framework 4.5 (KB2972107),1;Security Update for Microsoft .NET Framework 4.5 (KB2972216),1;Security Update for Microsoft .NET Framework 4.5 (KB2978128),1;Security Update for Microsoft .NET Framework 4.5 (KB2979578v2),2;Security Update for Microsoft .NET Framework 4.5 (KB3023224),1;Security Update for Microsoft .NET Framework 4.5 (KB3035490),1;Security Update for Microsoft .NET Framework 4.5 (KB3037581),1;Security Update for Microsoft .NET Framework 4.5 (KB3074230),1;Security Update for Microsoft .NET Framework 4.5 (KB3074550),1;Microsoft Office Outlook Connector,14.0.5118.5000;MySQL Examples and Samples 5.6,5.6.21;Realtek USB 2.0 Card Reader,6.1.7601.39019;MySQL Connector J,5.1.33;MySQL Connector/C 6.1,6.1.5;Visual Studio 2012 x86 Redistributables,14.0.0.1;CCC Help Finnish,2012.0305.0347.6610;MySQL Documents 5.6,5.6.21;Windows Live Writer,16.4.3528.0331;CCC Help Japanese,2012.0305.0347.6610;Microsoft Visual C++ 2005 Redistributable,8.0.50727.42;Google Update Helper,1.3.25.11;CCC Help Korean,2012.0305.0347.6610;Adobe Refresh Manager,1.8.0;Adobe Reader XI (11.0.12),11.0.12;Microsoft Visual C++ 2012 x86 Additional Runtime - 11.0.61030,11.0.61030;Windows Live PIMT Platform,16.4.3528.0331;Windows Live Mail,16.4.3528.0331;CCC Help Chinese Standard,2012.0305.0347.6610;CCC Help French,2012.0305.0347.6610;Windows Live Mail,16.4.3528.0331;Windows Live Messenger,16.4.3528.0331;Catalyst Control Center Profiles Mobile,2012.0305.348.6610;Microsoft Visual C++ 2012 x86 Minimum Runtime - 11.0.61030,11.0.61030;CCC Help German,2012.0305.0347.6610;CCC Help Danish,2012.0305.0347.6610;CCC Help Italian,2012.0305.0347.6610;ComProbe Protocol Analysis System,1.00.0000;Muses.WindowsClient.Service.Installer,1.0.0;Photo Gallery,16.4.3528.0331;Microsoft Visual C++ 2012 Redistributable (x64) - 11.0.61030,11.0.61030.0;Photo Common,16.4.3528.0331;MySQL Notifier 1.1.6,1.1.6;Windows Live SOXE,16.4.3528.0331;Catalyst Control Center - Branding,1.00.0000;Microsoft .NET Framework 4 Multi-Targeting Pack,4.0.30319;Update for  (KB2504637),1;MSVCRT_amd64,15.4.2862.0708;Windows Live SOXE Definitions,16.4.3528.0331;Catalyst Control Center Localization All,2012.0305.348.6610;CCC Help Portuguese,2012.0305.0347.6610;Microsoft SQL Server 2012 Management Objects ,11.0.2100.60;CCC Help Spanish,2012.0305.0347.6610;Movie Maker,16.4.3528.0331;MySQL Connector C++ 1.1.4,1.1.4;D3DX10,15.4.2368.0902;CCC Help Chinese Traditional,2012.0305.0347.6610;Microsoft System CLR Types for SQL Server 2012,11.0.2100.60;Windows Live Messenger,16.4.3528.0331;Cisco PEAP Module,1.1.6;Microsoft SQL Server 2005 Compact Edition [ENU],3.1.0000;Microsoft Visual C++ 2010  x86 Redistributable - 10.0.40219,10.0.40219;Intel(R) Display Audio Driver,6.14.00.3090;MSXML 4.0 SP2 (KB973688),4.20.9876.0;Microsoft SQL Server Data Tools Build Utilities - enu (11.1.20828.01),11.1.20828.01;Microsoft SQL Server 2012 Data-Tier App Framework ,11.0.2316.0;Microsoft Help Viewer 2.0,2.0.50727\",\"appname\":\"init\",\"packagestatus\":\"unknown\",\"appversion\":\"init\",\"id\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\",\"timestamp\":1444636112996}},\"id\":-482549072,\"username\":\"muses\"}";
		
		String sessionId= "092EF788C97FC21A19393443128EE40E";
		
		ConnectionCallbacksImpl callback = ConnectionCallbacksImpl.getInstance();
		callback.receiveCb(sessionId, testConfigSyncWindows);
		
		try {
			System.out.println("Sleep starts!");
			Thread.sleep(20000);
			System.out.println("Sleep ends!");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<ContextEvent> list = JSONManager.processJSONMessage(testWindows, "online_decision");
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator<ContextEvent> iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			JSONObject root;
			try {
			
				root = new JSONObject(testWindows);
				formattedEvent.setSessionId(sessionId);
				formattedEvent.setUsername(root
						.getString(JSONIdentifiers.AUTH_USERNAME));
				formattedEvent.setDeviceId(root
						.getString(JSONIdentifiers.AUTH_DEVICE_ID));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			formattedEvent.setSessionId(sessionId);
			des.insertFact(formattedEvent);
		}*/
	}


}
