Copyright (C) 2012-2013 Seamus Phelan <SeamusPhelan@gmail.com>

ProfileSwitcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ProfileSwitcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ProfileSwitcher.  If not, see <http://www.gnu.org/licenses/>.


App details
===========
This app allows you to setup schedules to switch profiles. This will only work on a ROM that has a profile 
manager (e.g. CyanogenMod 9 & 10 - see http://cyanogenmod.org). Features also include resetting the profile when headphones are pluged/unplugged and the ability to 
set a once-off 'timed profile' (similar to the old Nokias).

Prebuild APK
============
You can download the APK at http://forum.xda-developers.com/showthread.php?t=1856022 (and leave comments).


How to use
==========
Create schedules for when you want your phone to automatically change profiles. To create a schedule, chose "New schedule" in the menu. Choose the profile you want to switch, the time of day (24 hour clock) and which days of the week.
For example: Imagine you have profiles Work, Home and Night. You could have the following schedules.

* Work @ 08:00 on Monday, Tuesday, Wednesday, Thursday & Friday
* Home @ 10:30 on Saturday & Sunday
* Home @ 17:30 on Monday, Tuesday, Wednesday, Thursday & Friday
* Night @ 23:30 on Monday, Tuesday, Wednesday, Thursday, Friday, Saturday & Sunday


Compiling
=========
In order to compile you will need the JAR files that contain android.app.IProfileManager and android.app.ProfileManager.
Since these are only in the CyanogenMod ROM, you will need to compile them from the CyanogenMod sources (they will be 
under android/system/out/target/common/obj/JAVA_LIBRARIES/framework_intermediates/).

It may be possible to extract the jar file from your phone (assuming you are running CyanogenMod).  Try 
extracting classes.dex from /system/framework/framework.jar. (I haven't tried this but I reckon it would work)

profiles.xml
============
When upgrading (after a wipe), the profiles settings will be lost.  I don't know of a way to backup/restore them from 
within CyanogenMod.If you have a backup of the XML 
file (/system/etc/profiles.xml or /data/system/profiles.xml in CM10.1.2) that contains the profiles settings, you can restore 
it using abd. However the ring tones will be won't be pointing to the correct tones because it uses the MediaStore internal 
URI (eg content://media/internal/audio/media/92).  These internal URI change with each install. You will have to 
manually change the ringtones of each profile after copying.



