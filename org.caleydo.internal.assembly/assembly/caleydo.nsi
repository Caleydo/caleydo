;Caleydo NSIS scripted based on Modern UI

; include modern ui plugin and 64 bit detection
  !include "MUI2.nsh"
  !include "x64.nsh"
  !include "fileassoc.nsh"

;--------------------------------
;General

  ;Name and file
  !define APP_NAME "Caleydo"
  
  Name "${APP_NAME}"
  OutFile "Caleydo32.exe"

  ;Default installation folder
  InstallDir "$LOCALAPPDATA\${APP_NAME}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\${APP_NAME}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin
;--------------------------------
;Variables

  Var StartMenuFolder
;--------------------------------
;Interface Configuration
  !define MUI_ICON "caleydo.ico"
  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "caleydo_windows_installer.bmp"
  !define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
  !define MUI_HEADERIMAGE_RIGHT
  !define MUI_ABORTWARNING
  !define MUI_WELCOMEPAGE_TEXT "Caleydo is an open source visual analysis framework targeted at biomolecular data. The biggest strength of Caleydo is the visualization of interdependencies between multiple datasets. Caleydo can load tabular data and groupings/clusterings. You can explore relationships between multiple groupings, between different datasets and see how your data maps onto pathways.$\r$\n$\r$\nVisit http://caleydo.org to learn more."
  !define MUI_FINISHPAGE_TEXT "Thank you for installing Caleydo!$\r$\n$\r$\nGo to http://help.caleydo.org if you need help."

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
  !insertmacro MUI_PAGE_DIRECTORY
  
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${APP_NAME}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Dummy Section" SecDummy

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File /r "data\*.*"
  
  ;Store installation folder
  WriteRegStr HKCU "Software\${APP_NAME}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${APP_NAME}.lnk" "$INSTDIR\${APP_NAME}.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END
  
  ;Associate cal files 
  !insertmacro APP_ASSOCIATE "cal" "${APP_NAME}.project" "${APP_NAME} Project File" \
	"$INSTDIR\${APP_NAME}.exe,0" "Open with ${APP_NAME}" "$INSTDIR\${APP_NAME}.exe $\"%1$\""

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

Function .onInit
${If} ${RunningX64}
   DetailPrint "Installer running on 64-bit host"
   ; disable registry redirection (enable access to 64-bit portion of registry)
   SetRegView 64
   ; change install dir
   StrCpy $INSTDIR "$PROGRAMFILES64\${APP_NAME}"
${EndIf}

  SetOutPath $TEMP
  File /oname=spltmp.bmp "splash.bmp"

; optional
; File /oname=spltmp.wav "my_splashshit.wav"

  advsplash::show 1000 600 400 -1 $TEMP\spltmp

  Pop $0 ; $0 has '1' if the user closed the splash screen early,
         ; '0' if everything closed normally, and '-1' if some error occurred.

  Delete $TEMP\spltmp.bmp
;  Delete $TEMP\spltmp.wav
FunctionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
  DeleteRegKey /ifempty HKCU "SOFTWARE\${APP_NAME}"

  ; Remove files and uninstaller
  RMDir /r /REBOOTOK $INSTDIR
  
  ; Unassociate files
  !insertmacro APP_UNASSOCIATE "cal" "${APP_NAME}.project"

  ; Remove shortcuts, if any
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"

  ; Remove directories used
  RMDir "$SMPROGRAMS\${APP_NAME}"

SectionEnd