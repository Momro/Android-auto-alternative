# Car Dashboard (Android, Split-Screen / Multi-Window)

Leichte Companion-App für Android Multi-Window: Medienwiedergabe (MediaSession),
GPS-Tacho, Home-Assistant-Schnellaktionen und Navi-Start im benachbarten Fenster.

## Projektstruktur

```
.gitea/workflows/build-apk.yaml   # Gitea Actions: baut app-debug.apk bei Push auf main/master
app/
  build.gradle.kts                # AGP 8.5.2, Kotlin 2.0.21, Compose BOM 2024.06.00, minSdk 26 / target 34
  src/main/AndroidManifest.xml    # INTERNET, FINE/COARSE_LOCATION, POST_NOTIFICATIONS, NotificationListenerService
  src/main/java/com/example/cardashboard/
    MainActivity.kt               # FLAG_KEEP_SCREEN_ON, Runtime-Permission Location
    ui/DashboardViewModel.kt      # kombiniert Media-, Location- und HA-Flows
    ui/DashboardScreen.kt         # Landscape-Layout: MediaCard | Tacho + QuickActions + Launcher
    ui/components/                # MediaPlayerCard, SpeedCard, QuickActionsGrid, AppLauncherRow, SettingsDialog
    ui/theme/Theme.kt             # dunkles Material-3-Theme
    media/MediaSessionHandler.kt  # MediaSessionManager + MediaController.Callbacks
    media/MediaNotificationListener.kt  # NotificationListenerService (für Session-Zugriff)
    homeassistant/HomeAssistantClient.kt # REST: POST /api/services/<domain>/<service>
    location/LocationHandler.kt   # FusedLocationProviderClient -> km/h Flow
    settings/SettingsStore.kt     # EncryptedSharedPreferences (HA URL + Token)
    settings/QuickAction.kt       # Quick-Action-Modell + JSON (De-)Serialisierung
```

## Lokal bauen

```bash
gradle wrapper --gradle-version 8.7   # optional: erzeugt gradlew + gradle-wrapper.jar zum Committen
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Hinweis: Ist `gradlew` nicht committet, erzeugen die CI-Workflows den Wrapper vor dem Build automatisch.

## CI: APK bauen

- **GitHub Actions**: `.github/workflows/build-apk.yaml` baut bei jedem Push auf
  `main`/`master` (und manuell via *Run workflow*) die APK.
  Download: Repo -> Reiter **Actions** -> Run -> **Artifacts** -> `Dashboard-Car-App-Debug`.
- **Gitea Actions**: `.gitea/workflows/build-apk.yaml` macht dasselbe, sobald das Repo in
  Gitea gespiegelt ist und ein Runner mit Label `ubuntu-latest` registriert ist.

## Ersteinrichtung am Android-Gerät

1. Entwickleroptionen: **"Aktivitäten als größenveränderbar anpassen"** (Force activities to be
   resizable) aktivieren, dann Neustart.
2. App installieren, dann unter **Einstellungen -> Datenschutz -> Spezieller App-Zugriff ->
   Benachrichtigungszugriff** der Dashboard-App den Zugriff erteilen (oder über den
   Warnhinweis in der App).
3. In der App (Zahnrad) HA Base URL + Long-Lived Access Token eintragen
   (Profil -> Long-Lived Access Token in Home Assistant erstellen).
4. Dashboard und Navi (z.B. Magic Earth) im Split-Screen öffnen und als dauerhaftes
   App-Paar auf dem Homescreen ablegen.
5. Berechtigung `ACCESS_FINE_LOCATION` wird beim ersten Start abgefragt.
