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
	private final String testFullCycleWithClues = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testSecurityDeviceStateStep1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894992,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Kaspersky Mobile Security, com.kaspersky.mobile.security;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	private final String testSecurityDeviceStateStep2 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1403855894993,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.location, com.google.process.gapps, com.android.bluetooth, com.android.location.fused, com.android.bluetooth, com.google.process.gapps, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.bluetooth, com.android.vending, com.android.systemui, com.android.bluetooth, com.google.android.music:main, com.google.android.inputmethod.latin, com.google.android.music:main, eu.musesproject.client, com.google.process.location, com.google.android.apps.maps:GoogleLocationService, eu.musesproject.client, com.google.process.location, com.android.nfc:handover, system, com.google.process.location, com.google.process.location, com.android.systemui, com.google.process.gapps, com.android.bluetooth, com.android.phone]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"unknown\",\"timestamp\":1403854443665,\"bssid\":\"f8:1a:67:83:71:58\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"18\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backupconfirm;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1403855896071,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"insensitive\"}},\"requesttype\":\"online_decision\"}";
	
	private final String testUserAction = "{\"behavior\":{\"action\":\"cancel\"},\"requesttype\":\"user_behavior\"}";
	private final String testUserAction3 = "{\"behavior\":{\"action\":\"cancel\"},\"username\":\"muses\",\"device_id\":\"354401050109737\",\"requesttype\":\"user_behavior\"}";
	
	
	private final String testOpenConfAssetInSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WEP\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenConfAssetSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testOpenConfAssetSecureReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1411663474535,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, net.openvpn.openvpn, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather, com.lge.lmk, com.lge.lgfotaclient:remote]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411663462078,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411663482228,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"2\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411663462220,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,216;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6109034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51001051;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1411663483486,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"\\/sdcard\\/Swe\\/MUSES_partner_grades.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"package\":\"\",\"appname\":\"Gmail\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testBlacklistAppWifiAnalyzer = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Wifi Analyzer\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.wifi.analyzer\",\"appname\":\"Wifi Analyzer\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testNotBlacklistApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Other\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.other.app\",\"appname\":\"Other\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithoutAttachments = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 0,\"attachmentInfo\": \"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithAttachments = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 2,\"attachmentInfo\": \"name,type,size;name2,type2,size2\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testEmailWithAttachmentsReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"3586480519805834fd9ccf61\",\"requesttype\":\"online_decision\"}";
	private final String testVirusFound = "{\"sensor\":{},\"action\":{\"type\":\"virus_found\",\"timestamp\" : \"1389885147\",\"properties\": {\"path\":\"/sdcard/Swe/virus.txt\",\"name\":\"seriour_virus\",\"severity\":\"high\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testVirusFoundReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_found\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/Swe\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";

	private final String testOpenAssetUC6_user_muses_door1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/Swe/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenAssetUC6_user_muses2_door1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/Swe/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testOpenAssetUC6_user_muses3_door1 =	"{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1401986291588,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, com.android.defcontainer, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Sweden Connectivity\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"WPA2\",\"timestamp\":1401986235742,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1401986354214,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"/sdcard/Swe/door_1\",\"resourceName\":\"door_1\",\"resourceType\":\"CONFIDENTIAL\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";	
			
	private final String testScreenLockDisable = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410356356486,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410356610171,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"id\":\"1\",\"isrooted\":\"false\",\"isrootpermissiongiven\":\"false\",\"timestamp\":1410356610171,\"ipaddress\":\"172.17.1.52\",\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"true\",\"musesdatabaseexists\":\"true\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"2\",\"timestamp\":1410348330382,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;..._MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410356612042,\"type\":\"ACTION_SEND_MAIL\",\"properties\":{\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"subject\":\"MUSES sensor status subject\",\"path\":\"sdcard\",\"bcc\":\"hidden.reiceiver@generic.com\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testScreenLockDisableReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"120\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	private final String testScreenLockTimeoutReal = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	private final String testScreenLockDisableInAction = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410356356486,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410356610171,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"id\":\"1\",\"isrooted\":\"false\",\"isrootpermissiongiven\":\"false\",\"timestamp\":1410356610171,\"ipaddress\":\"172.17.1.52\",\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"true\",\"musesdatabaseexists\":\"true\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"2\",\"timestamp\":1410348330382,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;..._MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410356612042,\"type\":\"security_property_changed\",\"properties\":{\"property\":\"SCREEN_LOCK_TYPE\",\"value\":\"None\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testUninstallRequiredApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testDebug = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"1\",\"path\":\"\\/storage\\/emulated\\/0\\/Swe\\/Confidential\\/MUSES_confidential_doc.txt\",\"timestamp\":1412171145047,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"open\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1412171050085,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"192.168.35.199\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][WPS][ESS]\",\"timestamp\":1412171065588,\"bssid\":\"00:1c:f0:f1:b1:08\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"3\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1412171049660,\"installedapps\":\"Android System,android,17;com.android.backupconfirm,com.android.backupconfirm,17;Bluetooth Share,com.android.bluetooth,17;com.android.browser.provider,com.android.browser.provider,17;Calculator,com.android.calculator2,17;Certificate Installer,com.android.certinstaller,17;Chrome,com.android.chrome,1025469;Contacts,com.android.contacts,17;Package Access Helper,com.android.defcontainer,17;Basic Daydreams,com.android.dreams.basic,17;Face Unlock,com.android.facelock,17;HTML Viewer,com.android.htmlviewer,17;Input Devices,com.android.inputdevices,17;Key Chain,com.android.keychain,17;Launcher,com.android.launcher,17;Fused Location,com.android.location.fused,17;MusicFX,com.android.musicfx,10400;Nfc Service,com.android.nfc,17;Bubbles,com.android.noisefield,1;Package installer,com.android.packageinstaller,17;Phase Beam,com.android.phasebeam,1;Mobile Data,com.android.phone,17;Search Applications Provider,com.android.providers.applications,17;Calendar Storage,com.android.providers.calendar,17;Contacts Storage,com.android.providers.contacts,17;Download Manager,com.android.providers.downloads,17;Downloads,com.android.providers.downloads.ui,17;DRM Protected Content Storage,com.android.providers.drm,17;Media Storage,com.android.providers.media,511;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,17;Settings Storage,com.android.providers.settings,17;Mobile Network Configuration,com.android.providers.telephony,17;User Dictionary,com.android.providers.userdictionary,17;Settings,com.android.settings,17;com.android.sharedstoragebackup,com.android.sharedstoragebackup,17;System UI,com.android.systemui,17;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,17;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral,17;Live Wallpaper Picker,com.android.wallpaper.livepicker,17;avast! Mobile Security,com.avast.android.mobilesecurity,7801;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,20729;Currents,com.google.android.apps.currents,130141211;Google Play Magazines,com.google.android.apps.magazines,130212123;Maps,com.google.android.apps.maps,614020503;Google+,com.google.android.apps.plus,351420604;Picasa Uploader,com.google.android.apps.uploader,224000;Wallet,com.google.android.apps.walletnfcrel,301;Google Backup Transport,com.google.android.backup,17;Calendar,com.google.android.calendar,201210290;ConfigUpdater,com.google.android.configupdater,17;Clock,com.google.android.deskclock,203;Sound Search for Google Play,com.google.android.ears,6;Email,com.google.android.email,410000;Exchange Services,com.google.android.exchange,500000;Market Feedback Agent,com.google.android.feedback,17;Gallery,com.google.android.gallery3d,40001;Gmail,com.google.android.gm,650;Google Play services,com.google.android.gms,6109036;Google Search,com.google.android.googlequicksearchbox,210020210;Google Services Framework,com.google.android.gsf,17;Google Account Manager,com.google.android.gsf.login,17;Google Korean keyboard,com.google.android.inputmethod.korean,83;Android keyboard,com.google.android.inputmethod.latin,1700;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack,170;Google Pinyin,com.google.android.inputmethod.pinyin,23;Network Location,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,83;Google Play Music,com.google.android.music,912;Google One Time Init,com.google.android.onetimeinitializer,17;Google Partner Setup,com.google.android.partnersetup,17;Setup Wizard,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Google Contacts Sync,com.google.android.syncadapters.contacts,17;Tags,com.google.android.tag,101;Talk,com.google.android.talk,330;Google Text-to-speech Engine,com.google.android.tts,17;Movie Studio,com.google.android.videoeditor,11;Google Play Movies & TV,com.google.android.videos,23079;com.google.android.voicesearch,com.google.android.voicesearch,40000000;YouTube,com.google.android.youtube,4216;Earth,com.google.earth,12352100;Quickoffice,com.qo.android.tablet.oem,3;_MUSES,eu.musesproject.client,1;MusesClientTestTest,eu.musesproject.client.test,1;iWnn IME,jp.co.omronsoft.iwnnime.ml,6;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white,1\",\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"avast! Mobile Security\",\"packagestatus\":\"INSTALLED\",\"appversion\":\"7801\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1412171129236,\"appversion\":\"215\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.google.process.location, com.android.bluetooth, com.google.process.gapps, com.android.vending, com.google.android.talk, com.avast.android.mobilesecurity, com.android.bluetooth, com.google.android.inputmethod.latin, com.google.process.location, com.android.systemui, com.avast.android.mobilesecurity, com.avast.android.mobilesecurity, com.google.android.inputmethod.latin, com.google.android.gms.wearable, com.google.android.apps.maps:GoogleLocationService, com.avast.android.mobilesecurity, com.google.process.location, com.android.systemui, com.android.phone, com.estrongs.android.pop, com.google.process.location, com.android.bluetooth, com.android.location.fused, com.android.defcontainer, android.process.media, com.google.process.gapps, com.google.process.location, com.google.process.location, com.android.vending, com.avast.android.mobilesecurity, com.google.android.apps.maps, com.google.android.gms, com.google.process.gapps, com.android.bluetooth, eu.musesproject.client, com.google.process.location, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.nfc:handover, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.process.gapps, com.android.bluetooth]\",\"appname\":\"ES File Explorer\",\"packagename\":\"com.estrongs.android.pop\"}},\"action\":{\"timestamp\":1412171145047,\"type\":\"open_asset\",\"properties\":{\"id\":\"1\",\"path\":\"\\/storage\\/emulated\\/0\\/Swe\\/Confidential\\/MUSES_confidential_doc.txt\",\"fileevent\":\"open\"}},\"username\":\"muses\",\"device_id\":\"e3da52dbe610b684\",\"requesttype\":\"online_decision\"}";
	
	private final String testSaveFileInMonitoredFolder = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;B�squeda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicaci�n MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electr�nico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Tel�fono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informaci�n de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuraci�n de red m�vil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador m�vil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;B�squeda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;S�ntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducci�n de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;B�squeda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;C�mara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresi�n m�vil,com.sec.android.app.mobileprint,21;Reproductor de m�sica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de v�deo,com.sec.android.app.ve,4;Reproductor de v�deo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galer�a,com.sec.android.gallery3d,30682;Comando r�pido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (anal�gico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizaci�n de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"save_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testOpenFileInMonitoredFolder = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;B�squeda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicaci�n MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electr�nico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Tel�fono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informaci�n de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuraci�n de red m�vil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador m�vil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;B�squeda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;S�ntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducci�n de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;B�squeda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;C�mara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresi�n m�vil,com.sec.android.app.mobileprint,21;Reproductor de m�sica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de v�deo,com.sec.android.app.ve,4;Reproductor de v�deo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galer�a,com.sec.android.gallery3d,30682;Comando r�pido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (anal�gico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizaci�n de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testUninstall = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413548015422,\"appversion\":\"16\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Instalador de paquetes\",\"packagename\":\"com.android.packageinstaller\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413547383148,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"9\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413548019067,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"com.lge.livewallpaper.prince\",\"appname\":\"unknown\",\"packagestatus\":\"REMOVED\",\"appversion\":\"- 1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413548020523,\"type\":\"uninstall\",\"properties\":{\"id\":\"3\",\"packagestatus\":\"REMOVED\",\"appversion\":\"- 1\",\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"unknown\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testConfidentialSecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	private final String testConfidentialSecure1 = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556338789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547099406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-CCMP][WPS][ESS]\",\"timestamp\":1413556194415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556306246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556345697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testConfidentialUnsecure = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"WEP\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"resourcePath\":\"\\/sdcard\\/Demo\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"sensitive\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	

private final String testConfidentialFileSensor = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1413556337789,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, eu.musesproject.client, com.lge.systemserver, com.android.smspush, com.google.process.location, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, com.android.defcontainer, android.process.media, com.google.process.gapps, com.lge.sizechangable.musicwidget.widget, com.google.process.location, com.fermax.fermaxapp, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.location, com.google.process.gapps, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1413547098406,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"192.168.15.14\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"WEP\",\"timestamp\":1413556193415,\"bssid\":\"00:8e:f2:73:33:d6\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"1\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1413556305246,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,218;Wifi Analyzer,com.farproc.wifi.analyzer,107;Fermax,com.fermax.fermaxapp,1;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413263351;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,6174034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,33331;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,51003053;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;OpenVPN Connect,net.openvpn.openvpn,56;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1413556344697,\"type\":\"open_asset\",\"properties\":{\"path\":\"\\/sdcard\\/SWE\\/MUSES_confidential_doc.txt\",\"resourceName\":\"statistics\",\"resourceType\":\"null\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testVirusCleaned = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_cleaned\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/aware_app_remote_files\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\",\"clean_type\":\"quarantine\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testEmailWithoutAttachmentsHashId = "{\"sensor\":{},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\" : \"1389885147\",\"properties\": {\"from\":\"max.mustermann@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\",\"bcc\":\"hidden.reiceiver@generic.com\",\"subject\":\"MUSES sensor status subject\",\"noAttachments\" : 0,\"attachmentInfo\": \"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"id\":1976}";
	
	private final String testDatabaseObjects = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"0\",\"attachmentInfo\":\"\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1977}";
	
	private final String testVirusFoundHashId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1408434702014,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434690992,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"virus_found\",\"timestamp\":1408434706973,\"properties\":{\"path\":\"\\/sdcard\\/Swe\\/virus.txt\",\"severity\":\"high\",\"name\":\"serious_virus\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1978}";
	
	private final String testEmailWithAttachmentsHashId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"2\",\"timestamp\":1408434038945,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.android.defcontainer, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps, android.process.media]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1408434029656,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"6\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1408433959585,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;Busqueda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicacion MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electronico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Telefono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones busqueda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informacion de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuracion de red movil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador movil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Wifi Analyzer,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;Busqueda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicacion de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuracion para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuracion,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizacion de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizacion de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizacion de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;Sintesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproduccion de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Wifi Analyzer,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;Busqueda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;Camara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresion movil,com.sec.android.app.mobileprint,21;Reproductor de musica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de video,com.sec.android.app.ve,4;Reproductor de video,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galeria,com.sec.android.gallery3d,30682;Comando rapido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (analogico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizacion de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"type\":\"ACTION_SEND_MAIL\",\"timestamp\":1408434044686,\"properties\":{\"bcc\":\"hidden.reiceiver@generic.com\",\"to\":\"the.reiceiver@generic.com, another.direct.receiver@generic.com\",\"noAttachments\":\"1\",\"attachmentInfo\":\"pdf\",\"from\":\"max.mustermann@generic.com\",\"subject\":\"MUSES sensor status subject\",\"cc\":\"other.listener@generic.com, 2other.listener@generic.com\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\",\"id\":1979}";
	
	private final String testLogin = "{\"password\":\"muses\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"login\"}";
	
	private final String testLoginFailPass = "{\"password\":\"jsfdkjf\",\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"login\"}";
	
	private final String testLoginFailUser = "{\"password\":\"jsfdkjf\",\"device_id\":\"358648051980583\",\"username\":\"dfasdf\",\"requesttype\":\"login\"}";
	
	private final String testLoginSuccess = "{\"password\":\"pass\",\"device_id\":\"358648051980583\",\"username\":\"joe\",\"requesttype\":\"login\"}";
	
	private final String testLoginNotEnabled = "{\"password\":\"56836458345673465\",\"device_id\":\"358648051980583\",\"username\":\"notfound\",\"requesttype\":\"login\"}";
	
	//private final String testLoginNotEnabled = "{\"password\":\"swe_test\",\"device_id\":\"358648051980583\",\"username\":\"swe-tester-2\",\"requesttype\":\"login\"}";
	
	private final String testLogout = "{\"device_id\":\"358648051980583\",\"username\":\"muses\",\"requesttype\":\"logout\"}";
	
	private final String testAntivirusNotRunning = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"100\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testIsRooted = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"100\",\"isrooted\":\"true\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"true\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"70\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"isrooted\":\"true\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testNotProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPatternProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"true\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"false\",\"ispatternprotected\":\"true\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPasswordProtected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"true\",\"ispatternprotected\":\"false\",\"screentimeoutinseconds\":\"15\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"ispatternprotected\":\"false\",\"accessibilityenabled\":\"false\",\"screentimeoutinseconds\":\"15\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"local_decision\"}";
	
	private final String testPolicyOpenBlacklistGenericApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"uTorrent\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"open_application\",\"properties\":{\"packagename\":\"com.utorrent\",\"appname\":\"uTorrent\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	//private final String testUnsafeStorage = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.avast.android.mobilesecurity\",\"appname\":\"Avast\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testUnsafeStorage = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1403854423397,\"installedapps\":\"Android System,android;com.android.backupconfirm,com.android.backup;Bluetooth Share,com.android.bluetooth;com.android.browser.provider,com.android.browser.provider;Calculator,com.android.calculator2;Certificate Installer,com.android.certinstaller;Chrome,com.android.chrome;Contacts,com.android.contacts;Package Access Helper,com.android.defcontainer;Basic Daydreams,com.android.dreams.basic;Face Unlock,com.android.facelock;HTML Viewer,com.android.htmlviewer;Input Devices,com.android.inputdevices;Key Chain,com.android.keychain;Launcher,com.android.launcher;Fused Location,com.android.location.fused;MusicFX,com.android.musicfx;Nfc Service,com.android.nfc;Bubbles,com.android.noisefield;Package installer,com.android.packageinstaller;Phase Beam,com.android.phasebeam;Mobile Data,com.android.phone;Search Applications Provider,com.android.providers.applications;Calendar Storage,com.android.providers.calendar;Contacts Storage,com.android.providers.contacts;Download Manager,com.android.providers.downloads;Downloads,com.android.providers.downloads.ui;DRM Protected Content Storage,com.android.providers.drm;Media Storage,com.android.providers.media;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks;Settings Storage,com.android.providers.settings;Mobile Network Configuration,com.android.providers.telephony;User Dictionary,com.android.providers.userdictionary;Settings,com.android.settings;com.android.sharedstoragebackup,com.android.sharedstoragebackup;System UI,com.android.systemui;Google Play Store,com.android.vending;VpnDialogs,com.android.vpndialogs;com.android.wallpaper.holospiral,com.android.wallpaper.holospiral;Live Wallpaper Picker,com.android.wallpaper.livepicker;Google Play Books,com.google.android.apps.books;Currents,com.google.android.apps.currents;Google Play Magazines,com.google.android.apps.magazines;Maps,com.google.android.apps.maps;Google+,com.google.android.apps.plus;Picasa Uploader,com.google.android.apps.uploader;Wallet,com.google.android.apps.walletnfcrel;Google Backup Transport,com.google.android.backup;Calendar,com.google.android.calendar;ConfigUpdater,com.google.android.configupdater;Clock,com.google.android.deskclock;Sound Search for Google Play,com.google.android.ears;Email,com.google.android.email;Exchange Services,com.google.android.exchange;Market Feedback Agent,com.google.android.feedback;Gallery,com.google.android.gallery3d;Gmail,com.google.android.gm;Google Play services,com.google.android.gms;Google Search,com.google.android.googlequicksearchbox;Google Services Framework,com.google.android.gsf;Google Account Manager,com.google.android.gsf.login;Google Korean keyboard,com.google.android.inputmethod.korean;Android keyboard,com.google.android.inputmethod.latin;Dictionary Provider,com.google.android.inputmethod.latin.dictionarypack;Google Pinyin,com.google.android.inputmethod.pinyin;Network Location,com.google.android.location;TalkBack,com.google.android.marvin.talkback;Google Play Music,com.google.android.music;Google One Time Init,com.google.android.onetimeinitializer;Google Partner Setup,com.google.android.partnersetup;Setup Wizard,com.google.android.setupwizard;Street View,com.google.android.street;Google Contacts Sync,com.google.android.syncadapters.contacts;Tags,com.google.android.tag;Talk,com.google.android.talk;Google Text-to-speech Engine,com.google.android.tts;Movie Studio,com.google.android.videoeditor;Google Play Movies & TV,com.google.android.videos;com.google.android.voicesearch,com.google.android.voicesearch;YouTube,com.google.android.youtube;Earth,com.google.earth;Quickoffice,com.qo.android.tablet.oem;_MUSES,eu.musesproject.client;Sweden Connectivity,eu.musesproject.musesawareapp;iWnn IME,jp.co.omronsoft.iwnnime.ml;iWnnIME Keyboard (White),jp.co.omronsoft.iwnnime.ml.kbd.white\",\"packagename\":\"\",\"appname\":\"\",\"packagestatus\":\"init\",\"appversion\":\"\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"uninstall\",\"properties\":{\"packagename\":\"com.other.android.other\",\"appname\":\"Other\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testInstallNotAllowedApp = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	//private final String testInZone = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"true\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	private final String testInZone = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1402313215730,\"type\":\"install\",\"properties\":{\"packagename\":\"com.p2p.vuze\",\"appname\":\"Vuze\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testZone1Restriction = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"open_application\",\"timestamp\":1428357713480,\"properties\":{\"packagename\":\"com.google.android.GoogleCamera\",\"appname\":\"Camara\",\"package\":\"\",\"version\":\"\"}},\"requesttype\":\"online_decision\",\"device_id\":\"358648051980583\",\"username\":\"muses\"}";
	
	private final String testOpenFileInMonitoredFolderInRestrictedZone = "{\"sensor\":{\"CONTEXT_SENSOR_FILEOBSERVER\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"timestamp\":1411480677967,\"type\":\"CONTEXT_SENSOR_FILEOBSERVER\",\"fileevent\":\"close_write\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1411480566746,\"ispasswordprotected\":\"true\",\"screentimeoutinseconds\":\"300\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.52\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1411480657369,\"bssid\":\"24:a4:3c:04:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1411480566862,\"installedapps\":\"PS Touch,air.com.adobe.pstouch.oem1,1004002;Sistema Android,android,16;B�squeda de Google,android.googleSearch.googleSearchWidget,1;Crayon physics,com.acrodea.crayonphysics,1;Aplicaci�n MTP,com.android.MtpApplication,1;Preconfig,com.android.Preconfig,16;com.android.backupconfirm,com.android.backupconfirm,16;Compartir Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,16;Calendario,com.android.calendar,16;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1985131;TestService,com.android.clipboardsaveservice,1;Contactos,com.android.contacts,16;Ayudante acceso a paquete,com.android.defcontainer,16;Correo electr�nico,com.android.email,410000;Servicios de Exchange,com.android.exchange,410000;Desbloqueo facial,com.android.facelock,16;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;MusicFX,com.android.musicfx,10400;Bubbles,com.android.noisefield,1;Instalador de paquete,com.android.packageinstaller,16;Phase beam,com.android.phasebeam,1;Tel�fono,com.android.phone,16;PickupTutorial,com.android.pickuptutorial,16;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento de calendario,com.android.providers.calendar,16;Informaci�n de los contactos,com.android.providers.contacts,16;Descargas,com.android.providers.downloads,16;Descargas,com.android.providers.downloads.ui,16;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento de medios,com.android.providers.media,513;Almacen. de seguridad,com.android.providers.security,16;Almacenamiento de ajustes,com.android.providers.settings,16;Configuraci�n de red m�vil,com.android.providers.telephony,16;User Dictionary,com.android.providers.userdictionary,16;Enterprise SysScope Service,com.android.server.device.enterprise,16;Enterprise VPN Services,com.android.server.vpn.enterprise,16;Ajustes,com.android.settings,16;Rastreador m�vil,com.android.settings.mt,1;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;Grabadora de sonidos,com.android.soundrecorder,16;Kit herramientas SIM,com.android.stk,16;IU�sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondos de pantalla en movimiento,com.android.wallpaper.livepicker,16;Paper Artist,com.dama.paperartist,1002043;Popup Note,com.diotek.mini_penmemo,6074;Dropbox,com.dropbox.android,240200;SipDemo,com.example.android.sip,0;Wifi Analyzer,com.farproc.wifi.analyzer,104;Fermax,com.fermax.fermaxapp,1;FermaxappTestTest,com.fermax.fermaxapp.test,1;Controles remotos,com.fmm.dm,2;Controles remotos,com.fmm.ds,1;Google Play Books,com.google.android.apps.books,30149;Google Play Kiosco,com.google.android.apps.magazines,2014051213;Maps,com.google.android.apps.maps,802003401;Google+,com.google.android.apps.plus,413153783;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089032;B�squeda de Google,com.google.android.googlequicksearchbox,300305160;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;TalkBack,com.google.android.marvin.talkback,107;Google Play Music,com.google.android.music,1617;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20110032;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Hangouts,com.google.android.talk,21317130;S�ntesis de Google,com.google.android.tts,210030103;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5741;Aurora 2,com.hu1.wallpaper.aurora2,1;Polaris Office,com.infraware.PolarisOfficeStdForTablet,2077500580;Recortar,com.lifevibes.trimapp,1;ChocoEUKor,com.monotype.android.font.chococooky,1;CoolEUKor,com.monotype.android.font.cooljazz,1;Helv Neue S,com.monotype.android.font.helvneuelt,1;RoseEUKor,com.monotype.android.font.rosemary,1;CatLog,com.nolanlawson.logcat,42;Fermax example,com.okode.linphone,1;Samsung account,com.osp.app.signin,140266;Peel Smart Remote,com.peel.app,30342;VideoStreaming,com.s2.videostreaming,1;SpaceIT,com.s2grupo.spaceit,1;Samsung TTS,com.samsung.SMT,0;Ajustes de licencia,com.samsung.android.app.divx,0;Deep sea,com.samsung.android.livewallpaper.deepsea,1;Luminous dots,com.samsung.android.livewallpaper.luminousdots,1;com.samsung.app.playreadyui,com.samsung.app.playreadyui,1;AvrcpServiceSamsung,com.samsung.avrcp,16;Reproducci�n de grupo,com.samsung.groupcast,1005058;Ayuda,com.samsung.helphub,1;Music Hub,com.samsung.music,1;INDIServiceManager,com.samsung.scrc.idi.server,2290904;CSC,com.samsung.sec.android.application.csc,16;Idea Sketch,com.samsung.sec.sketch,2;ShareShotService,com.samsung.shareshot,1;Gmail,com.samsung.spell.gmail,1;Maps,com.samsung.spell.googlemaps,1;B�squeda de Google,com.samsung.spell.googlesearch,1;Topic Wall,com.samsung.topicwall,1;Video Hub,com.samsung.videohub,1193;Ajustes USB,com.sec.android.Kies,1;AllShare Service,com.sec.android.allshare.framework,10;DataCreate,com.sec.android.app.DataCreate,1;Wi-Fi Direct,com.sec.android.app.FileShareClient,1;Uso compartido de Wi-Fi Direct,com.sec.android.app.FileShareServer,1;SecSetupWizard,com.sec.android.app.SecSetupWizard,1;com.sec.android.app.SuggestionService,com.sec.android.app.SuggestionService,1;BluetoothTest,com.sec.android.app.bluetoothtest,1;C�mara,com.sec.android.app.camera,1;Alarma,com.sec.android.app.clockpackage,1;Administrador de tareas,com.sec.android.app.controlpanel,1;Factory Mode,com.sec.android.app.factorymode,1;Game Hub,com.sec.android.app.gamehub,13010801;Kies mediante Wi-Fi,com.sec.android.app.kieswifi,2;Inicio TouchWiz,com.sec.android.app.launcher,16;Lcdtest,com.sec.android.app.lcdtest,1;com.sec.android.app.minimode.res,com.sec.android.app.minimode.res,16;Impresi�n m�vil,com.sec.android.app.mobileprint,21;Reproductor de m�sica,com.sec.android.app.music,1;Mis archivos,com.sec.android.app.myfiles,1;Perso,com.sec.android.app.personalization,16;PhoneUtil,com.sec.android.app.phoneutil,1;Calculadora,com.sec.android.app.popupcalculator,1;PopupuiReceiver,com.sec.android.app.popupuireceiver,1;Samsung Apps,com.sec.android.app.samsungapps,4700060;SamsungAppsUNA2,com.sec.android.app.samsungapps.una2,2035;Self Test Mode,com.sec.android.app.selftestmode,1;Service mode,com.sec.android.app.servicemodeapp,16;Nota S,com.sec.android.app.snotebook,1309093781;SNS,com.sec.android.app.sns3,10;SurfSetProp,com.sec.android.app.surfsetprop,1;SysScope,com.sec.android.app.sysscope,4;TwDVFSApp,com.sec.android.app.twdvfs,1;Editor de v�deo,com.sec.android.app.ve,4;Reproductor de v�deo,com.sec.android.app.videoplayer,1;SecWallpaperChooser,com.sec.android.app.wallpaperchooser,16;com.sec.android.app.wfdbroker,com.sec.android.app.wfdbroker,100;WlanTest,com.sec.android.app.wlantest,1;Reloj mundial,com.sec.android.app.worldclock,1;CloudAgent,com.sec.android.cloudagent,1;Dropbox,com.sec.android.cloudagent.dropboxoobe,1;ContextAwareService,com.sec.android.contextaware,16;Weather Daemon,com.sec.android.daemonapp.ap.accuweather,1;News Daemon(EUR),com.sec.android.daemonapp.ap.yahoonews,1;Yahoo! Finance Daemon,com.sec.android.daemonapp.ap.yahoostock.stockclock,1;DirectShareManager,com.sec.android.directshare,2;OmaDrmPopup,com.sec.android.drmpopup,1;Actualizac de software,com.sec.android.fotaclient,1;AllShare Cast Dongle S\\/W Update,com.sec.android.fwupgrade,1800011;Galer�a,com.sec.android.gallery3d,30682;Comando r�pido,com.sec.android.gesturepad,1;Teclado Samsung,com.sec.android.inputmethod,1;Pruebe el desplazamiento,com.sec.android.motions.settings.panningtutorial,1;Dispositivos cercanos,com.sec.android.nearby.mediaserver,16;Application installer,com.sec.android.preloadinstaller,1;BadgeProvider,com.sec.android.provider.badge,1;LogsProvider,com.sec.android.provider.logsprovider,16;Nota S,com.sec.android.provider.snote,1304012187;com.sec.android.providers.downloads,com.sec.android.providers.downloads,16;Copia de seg. y restaur.,com.sec.android.sCloudBackupApp,131;Samsung Backup Provider,com.sec.android.sCloudBackupProvider,14;Samsung Cloud Data Relay,com.sec.android.sCloudRelayData,201008;Samsung Syncadapters,com.sec.android.sCloudSync,269;Samsung Browser SyncAdapter,com.sec.android.sCloudSyncBrowser,1;Samsung Calendar SyncAdapter,com.sec.android.sCloudSyncCalendar,1;Samsung Contact SyncAdapter,com.sec.android.sCloudSyncContacts,1;Samsung SNote SyncAdapter,com.sec.android.sCloudSyncSNote,1;SASlideShow,com.sec.android.saslideshow,1;CapabilityManagerService,com.sec.android.service.cm,2;Ajustes,com.sec.android.signaturelock,1;Widget de Planificador S,com.sec.android.widgetapp.SPlannerAppWidget,1;AllShare Cast,com.sec.android.widgetapp.allsharecast,1;Reloj (moderno),com.sec.android.widgetapp.analogclocksimple,1;Yahoo! News,com.sec.android.widgetapp.ap.yahoonews,1;Weather Widget Main,com.sec.android.widgetapp.at.hero.accuweather,1;Weather Widget,com.sec.android.widgetapp.at.hero.accuweather.widget,1;Yahoo! Finance,com.sec.android.widgetapp.at.yahoostock.stockclock,1;Reloj digital,com.sec.android.widgetapp.digitalclock,1;Reloj dual (anal�gico),com.sec.android.widgetapp.dualclockanalog,1;Reloj dual (digital),com.sec.android.widgetapp.dualclockdigital,1;Monitor de aplicaciones,com.sec.android.widgetapp.programmonitorwidget,1;Manual de usuario,com.sec.android.widgetapp.webmanual,1;Error,com.sec.app.RilErrorNotifier,1;com.sec.bcservice,com.sec.bcservice,1;ChatON,com.sec.chaton,300450243;DSMForwarding,com.sec.dsm.phone,16;DSMLawmo,com.sec.dsm.system,1;EnterprisePermissions,com.sec.enterprise.permissions,1;Factory Test,com.sec.factory,1;MiniTaskcloserService,com.sec.minimode.taskcloser,1;Learning Hub,com.sec.msc.learninghub,13072501;AllShare Play,com.sec.pcw,3302;Remote Controls,com.sec.pcw.device,40;com.sec.phone,com.sec.phone,1;FlashAnnotate,com.sec.spen.flashannotate,1;FlashAnnotateSvc,com.sec.spen.flashannotatesvc,1;Samsung Push Service,com.sec.spp.push,91;Muro de fotos,com.siso.photoWall,1;SyncmlDS,com.smlds,1;Explorer,com.speedsoftware.explorer,34;S Suggest,com.tgrape.android.radar,3286;Ping & DNS,com.ulfdittmer.android.ping,79;Resource Manager,com.visionobjects.resourcemanager,1;S Voice,com.vlingo.midas,1000;OMACP,com.wsomacp,4;wssyncmlnps,com.wssnps,2;Actualizaci�n de software,com.wssyncmldm,2;OpenVPN Remote,de.blinkt.openvpn.remote,0;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;MusesAwareAppTestTest,eu.musesproject.musesawareapp.test,1;Shark,lv.n3o.shark,102;SharkReader,lv.n3o.sharkreader,15;OpenVPN Connect,net.openvpn.openvpn,56;Alfresco,org.alfresco.mobile.android.application,30;SmartcardService,org.simalliance.openmobileapi.service,2;VLC,org.videolan.vlc.betav7neon,9800\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"},\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1411480658748,\"appversion\":\"34\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.phone, com.google.process.gapps, com.android.smspush, system, com.sec.spp.push, com.google.android.talk, com.android.systemui, com.dropbox.android:crash_uploader, com.google.android.music:main, com.google.android.gms.wearable, android.process.media, com.google.android.gms, com.android.phone, com.tgrape.android.radar, com.android.phone, com.android.systemui, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, android.process.media, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.fermax.fermaxapp, com.android.phone, com.google.android.gms, com.wssyncmldm, com.google.android.music:main, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, eu.musesproject.client, com.android.phone, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.gapps, com.google.process.location, com.google.process.location, com.sec.android.app.videoplayer, com.google.process.gapps]\",\"appname\":\"Explorer\",\"packagename\":\"com.speedsoftware.explorer\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"timestamp\":1411480677967,\"type\":\"open_asset\",\"properties\":{\"id\":\"3\",\"path\":\"\\/storage\\/sdcard0\\/Swe\\/companyfile.txt\",\"fileevent\":\"close_write\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testEventForSessionId = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1410881508419,\"appversion\":\"3000\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone, com.google.process.gapps, com.lge.systemserver, com.android.smspush, com.google.android.talk, com.android.systemui, com.google.android.music:main, com.google.android.gms.wearable, com.lge.systemserver, com.lge.ime, com.lge.osp, com.google.android.gms, com.lge.lmk, system, com.lge.sync, com.android.phone, com.android.systemui, com.lge.ime, com.lge.ime, com.lge.systemserver, com.estrongs.android.pop, com.google.process.location, com.lge.music, android.process.media, com.lge.sizechangable.musicwidget.widget, com.google.process.gapps, com.google.process.location, com.google.android.music:main, com.lge.lgfotaclient:remote, eu.musesproject.client, com.lge.mlt, com.google.process.gapps, eu.musesproject.client, com.android.phone, system, com.google.process.gapps, com.google.process.location, com.google.process.location, com.google.android.tts, com.lge.smartshare.service, com.google.process.gapps, com.lge.sizechangable.weather]\",\"appname\":\"Ajustes\",\"packagename\":\"com.android.settings\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1410881608830,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"120\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"172.17.1.71\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"mobileconnected\":\"false\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1410881601947,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"0\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"3\",\"timestamp\":1410880985061,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,1500094;Contactos,com.android.contacts,30904903;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galer�a,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;LocMgrPD,com.android.locmgrpd,16;LocMgrPT,com.android.locmgrpt,16;Mensajes,com.android.mms,30500078;Servicio NFC,com.android.nfc,16;Instalador de paquetes,com.android.packageinstaller,16;Tel�fono,com.android.phone,30241103;Proveedor aplicaciones b�squeda,com.android.providers.applications,16;Almacenamiento en el calendario,com.android.providers.calendar,30300003;Informaci�n de los contactos,com.android.providers.contacts,30904903;Administrador de descargas,com.android.providers.downloads,1;Descargas,com.android.providers.downloads.ui,1;Almacenamiento de contenido protegido por DRM,com.android.providers.drm,16;Almacenamiento multimedia,com.android.providers.media,509;com.android.providers.partnerbookmarks,com.android.providers.partnerbookmarks,16;com.android.providers.settings,com.android.providers.settings,16;Almacenamiento tel�fono\\/mensajes,com.android.providers.telephony,30500016;User Dictionary,com.android.providers.userdictionary,16;Ajustes,com.android.settings,3000;com.android.sharedstoragebackup,com.android.sharedstoragebackup,16;com.android.smspush,com.android.smspush,16;,com.android.stk,16;IU sistema,com.android.systemui,16;Google Play Store,com.android.vending,80290013;VpnDialogs,com.android.vpndialogs,16;Selector de fondo de pantalla animado,com.android.wallpaper.livepicker,20000001;Bluetooth Services,com.broadcom.bt.app.system,1;Quick Translator,com.cardcam.QTranslator,30100024;Diccionario,com.diotek.diodict3.phone.lg.lgedict,30100061;ES File Explorer,com.estrongs.android.pop,215;Google Play Books,com.google.android.apps.books,30149;Drive,com.google.android.apps.docs,2022251;Maps,com.google.android.apps.maps,700022100;Google+,com.google.android.apps.plus,413201834;Picasa Uploader,com.google.android.apps.uploader,40000;Google Backup Transport,com.google.android.backup,16;Agente comentarios Market,com.google.android.feedback,16;Gmail,com.google.android.gm,4900120;Servicios de Google Play,com.google.android.gms,5089034;B�squeda de Google,com.google.android.googlequicksearchbox,300206070;Marco de servicios de Google,com.google.android.gsf,16;Administrador de cuentas de Google,com.google.android.gsf.login,16;Ubicaci�n de red,com.google.android.location,1110;Google Play Music,com.google.android.music,1623;Configuraci�n para partners de Google,com.google.android.partnersetup,16;Google Play Games,com.google.android.play.games,20130034;Asistente de configuraci�n,com.google.android.setupwizard,130;Street View,com.google.android.street,18102;Sincronizaci�n de Google Bookmarks,com.google.android.syncadapters.bookmarks,16;Sincronizaci�n de Google Calendar,com.google.android.syncadapters.calendar,16;Sincronizaci�n de contactos de Google,com.google.android.syncadapters.contacts,16;Etiquetas,com.google.android.tag,101;Hangouts,com.google.android.talk,753199;S�ntesis de Google,com.google.android.tts,210302120;Google Play Movies,com.google.android.videos,32251;com.google.android.voicesearch,com.google.android.voicesearch,4000000;YouTube,com.google.android.youtube,5090013;Font Server,com.hy.system.fontserver,16;Polaris Office 4,com.infraware.polarisoffice,306909;QuickMemo,com.lge.QuickClip,30100019;AAT,com.lge.allautotest,16;QSlide Framework,com.lge.app.floating.res,45;Nota,com.lge.app.richnote,30200118;com.lge.appbox.bridge,com.lge.appbox.bridge,100003;Gestor Aplic.,com.lge.appbox.client,300619;com.lge.appbox.remote,com.lge.appbox.remote,1;C�mara,com.lge.camera,10010001;Camera Test,com.lge.cameratest,1;Alarma,com.lge.clock,30000045;com.lge.defaultaccount,com.lge.defaultaccount,30000000;DRM Service,com.lge.drmservice,16;E-mail,com.lge.email,42170;Gestor archivos,com.lge.filemanager,15053;Radio FM,com.lge.fmradio,548;com.lge.hiddenmenu,com.lge.hiddenmenu,1;com.lge.hiddenpersomenu,com.lge.hiddenpersomenu,16;Selector de inicio,com.lge.homeselector,30025;Teclado LG,com.lge.ime,20200355;com.lge.internal,com.lge.internal,200;Inicio,com.lge.launcher2,301353;Home Theme - Biz,com.lge.launcher2.theme.biz,31130147;Home Theme - Cozywall,com.lge.launcher2.theme.cozywall,31130147;Home Theme - Marshmallow,com.lge.launcher2.theme.marshmallow,31130147;com.lge.lgdrm.permission,com.lge.lgdrm.permission,16;com.lge.lgfota.permission,com.lge.lgfota.permission,16;Actualizar SW,com.lge.lgfotaclient,3;LGInstallService,com.lge.lginstallservies,13013;SmartWorld,com.lge.lgworld,21120;Pluma,com.lge.livewallpaper.feather,30000037;Oso polar,com.lge.livewallpaper.polarbear,30000072;El Principito,com.lge.livewallpaper.prince,30000017;Admin. tareas,com.lge.lmk,30200009;com.lge.lockscreen,com.lge.lockscreen,3;Ajuste de bloqueo de pantalla,com.lge.lockscreensettings,30028000;LG MLT,com.lge.mlt,1;M�sica,com.lge.music,32019;Configuraci�n de On-Screen,com.lge.osp,30000100;PC Suite UI,com.lge.pcsyncui,30000002;com.lge.permission,com.lge.permission,1;LocMgrProvider,com.lge.provider.locmgr,16;Correcci�n de la relaci�n de aspecto,com.lge.settings.compatmode,16;Shutdown Monitor,com.lge.shutdownmonitor,16;Emails,com.lge.sizechangable.email,30100031;Widget de contactos favoritos,com.lge.sizechangable.favoritecontacts,3114;Notas,com.lge.sizechangable.memo,30200045;M�sica,com.lge.sizechangable.musicwidget.widget,31013;�lbum fot.,com.lge.sizechangable.photoalbum,3112;Tiempo,com.lge.sizechangable.weather,3128;Icono de reloj mundial,com.lge.sizechangable.worldclock,30100021;LGApduService,com.lge.smartcard.apdu,1;SmartShare,com.lge.smartshare,205006;Reproductor multimedia,com.lge.streamingplayer,19018;PC Suite Service,com.lge.sync,30000006;LGSystemServer,com.lge.systemservice,1;V�deos,com.lge.videoplayer,33026;Grabador de voz,com.lge.voicerecorder,32019;Servicio WAP,com.lge.wapservice,1;Widevine Keybox Test,com.lge.wv.hidden,16;Backup,com.spritemobile.backup.lg,4117;SystemBackupService,com.spritemobile.system.backup,112;Ping & DNS,com.ulfdittmer.android.ping,81;_MUSES,eu.musesproject.client,1;Sweden Connectivity,eu.musesproject.musesawareapp,1;FileShare,itectokyo.fileshare.ics20,20100031;Direct Beam,itectokyo.wiflus.directbeam,10000020;LGSmartcardService,org.simalliance.openmobileapi.service,3\",\"packagename\":\"eu.musesproject.musesawareapp\",\"appname\":\"Sweden Connectivity\",\"packagestatus\":\"UPDATED\",\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1410881609300,\"type\":\"security_property_changed\",\"properties\":{\"ispasswordprotected\":\"true\",\"accessibilityenabled\":\"true\",\"screentimeoutinseconds\":\"1\",\"istrustedantivirusinstalled\":\"true\",\"ipaddress\":\"172.17.1.71\",\"musesdatabaseexists\":\"true\"}},\"username\":\"muses\",\"device_id\":\"358648051980583\",\"id\":1976,\"requesttype\":\"local_decision\"}";
	
	private final String testOpenConfidentialAware = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1430827029134,\"appversion\":\"1\",\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.phone]\",\"appname\":\"Sweden Connectivity\",\"packagename\":\"eu.musesproject.musesawareapp\"},\"CONTEXT_SENSOR_DEVICE_PROTECTION\":{\"timestamp\":1430826989605,\"ispasswordprotected\":\"false\",\"screentimeoutinseconds\":\"30\",\"isrooted\":\"false\",\"musesdatabaseexists\":\"true\",\"accessibilityenabled\":\"false\",\"istrustedantivirusinstalled\":\"false\",\"ipaddress\":\"10.122.77.183\",\"type\":\"CONTEXT_SENSOR_DEVICE_PROTECTION\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"mobileconnected\":\"true\",\"wifiencryption\":\"unknown\",\"timestamp\":1430827029741,\"bssid\":\"null\",\"bluetoothconnected\":\"TRUE\",\"wifienabled\":\"true\",\"wifineighbors\":\"10\",\"hiddenssid\":\"false\",\"networkid\":\"-1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_PACKAGE\":{\"id\":\"1\",\"timestamp\":1430826989619,\"installedapps\":\"Sistema Android,android,16;LGSetupWizard,com.android.LGSetupWizard,43014;com.android.backupconfirm,com.android.backupconfirm,16;Compartir con Bluetooth,com.android.bluetooth,16;Internet,com.android.browser,1;Calculadora,com.android.calculator2,30000037;Calendario,com.android.calendar,30300813;Cell broadcast,com.android.cellbroadcastreceiver,20100089;Instalador de certificados,com.android.certinstaller,16;Chrome,com.android.chrome,2311111;Contactos,com.android.contacts,30902902;Ayudante acceso a paquete,com.android.defcontainer,16;Desbloqueo facial,com.android.facelock,16;Galera,com.android.gallery3d,40000032;Visor de HTML,com.android.htmlviewer,16;Input Devices,com.android.inputdevices,16;Key Chain,com.android.keychain,16;Battery Solo Widget,net.maicas.android.batterys,23;PassAndroid,org.ligi.passandroid,254;cryptonite,LGSmartcardService,org.simalliance.openmobileapi.service,3;Amazon,uk.amazon.mShop.android,502010\",\"packagename\":\"init\",\"appname\":\"init\",\"packagestatus\":\"init\",\"appversion\":\"-1\",\"type\":\"CONTEXT_SENSOR_PACKAGE\"}},\"action\":{\"timestamp\":1430827037341,\"type\":\"open_asset\",\"properties\":{\"path\":\"/sdcard/aware_app_remote_files/MUSES_confidential_doc.pdf\",\"resourceName\":\"statistics\",\"resourceType\":\"CONFIDENTIAL\"}},\"id\":394997978,\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
	private final String testUserEnteredPassword = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"user_entered_password_field\",\"timestamp\":1428357713480,\"properties\":{\"packagename\":\"com.google.android.gm\"}},\"requesttype\":\"online_decision\",\"device_id\":\"36474929437562939\",\"username\":\"muses\"}";
	
	private final String testUSBDeviceConnected = "{\"sensor\":{\"CONTEXT_SENSOR_APP\":{\"id\":\"3\",\"timestamp\":1402313215730,\"type\":\"CONTEXT_SENSOR_APP\",\"backgroundprocess\":\"[com.android.server.device.enterprise:remote, com.android.cryptonite, com.google.process.gapps, com.google.android.gms.drive, com.android.smspush, com.samsung.music, system, com.sec.spp.push, com.google.android.talk, com.google.process.location, com.android.systemui, com.google.android.gms, com.google.android.apps.maps, com.android.phone, com.sec.android.app.controlpanel, com.tgrape.android.radar, com.android.phone, com.samsung.music, com.android.systemui, com.wssnps, com.google.android.googlequicksearchbox:search, com.android.settings, com.sec.android.app.twdvfs, com.android.bluetooth, com.google.process.location, com.sec.android.inputmethod, com.google.android.youtube, android.process.media, com.google.android.gms, com.sec.phone, com.sec.msc.learninghub, com.google.process.gapps, com.sec.factory, com.google.process.location, com.android.server.vpn.enterprise:remote, com.android.phone, com.sec.android.widgetapp.at.hero.accuweather.widget:remote, eu.musesproject.client, com.android.MtpApplication, com.vlingo.midas, com.google.process.gapps, com.google.android.gms, eu.musesproject.client, com.android.phone, net.openvpn.openvpn, com.android.phone, system, com.sec.android.app.sysscope, com.google.process.location, com.google.process.location, com.samsung.videohub, com.google.android.tts, com.google.android.gm, com.sec.android.app.videoplayer, com.google.android.gms, com.google.process.gapps]\",\"appname\":\"Gmail\"},\"CONTEXT_SENSOR_CONNECTIVITY\":{\"id\":\"3\",\"wifiencryption\":\"[WPA2-PSK-TKIP+CCMP][ESS]\",\"timestamp\":1402313210321,\"bssid\":\"24:a4:3c:03:ae:09\",\"bluetoothconnected\":\"FALSE\",\"wifienabled\":\"true\",\"wifineighbors\":\"8\",\"hiddenssid\":\"false\",\"networkid\":\"1\",\"type\":\"CONTEXT_SENSOR_CONNECTIVITY\",\"wificonnected\":\"true\",\"airplanemode\":\"false\"},\"CONTEXT_SENSOR_LOCATION\":{\"id\":\"3\",\"timestamp\":1402313210321,\"isWithinZone\":\"1,2\",\"type\":\"CONTEXT_SENSOR_LOCATION\"}},\"action\":{\"type\":\"usb_device_connected\",\"timestamp\":1428357713480,\"properties\":{\"connected_via_usb\":\"true\"}},\"requesttype\":\"online_decision\",\"device_id\":\"36474929437562939\",\"username\":\"muses\", \"id\":\"111\" }";

	private final String testAddEmasNote = "{\"sensor\":{},\"action\":{\"timestamp\":1433518371811,\"type\":\"add_note\",\"properties\":{\"id_event\":\"holala\",\"id_user\":\"1849\",\"description\":\"G\",\"title\":\"g\"}},\"id\":-775048452,\"username\":\"muses\",\"device_id\":\"358648051980583\",\"requesttype\":\"online_decision\"}";
	
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
			des.insertFact(formattedEvent);
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
		
		/*EventProcessor processor = null;
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
		}*/
		

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
			des.insertFact(formattedEvent);
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
			des.insertFact(formattedEvent);
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
			des.insertFact(formattedEvent);
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
			des.insertFact(formattedEvent);
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

		
			UserContextEventDataReceiver.getInstance().processContextEventList(
				list, defaultSessionId, username, deviceId, requestId);

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
			des.insertFact(formattedEvent);
		}
	}

}
