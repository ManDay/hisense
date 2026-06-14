#!/bin/sh

# It is recommended to perform theses steps one-by-one manually, to make sure everything works
# This script does no exception handling of any sorts!
exit 0

# The GSI
image=/home/manday/download/android/system.img

# An empty directory where the image will be mounted for modification
mntpoint=/mnt/loop

# The path to the `a9srv` binary
a9srvbin=/home/manday/download/android/a9srv

# An image of the original Hisense "system_ext" partition (either from the A or B slot)
systemext=system_ext_a.img

# An image of the original Hisense "vendor" partition (either from the A or B slot)
vendor=vendor_a.img

# Where to store the resulting super image which can be flashed
super=/tmp/new_super.img

###

# If you really want to try it automatically, make sure it's run as root
if [[ "$EUID" -ne 0 ]]
then
 echo "Must be run as root" >&2
 exit 1
fi

e2fsck -y -f "$image"
resize2fs "$image" 4G
e2fsck -E unshare_blocks -y -f "$image"
e2fsck -y -f "$image"

mount "$image" "$mntpoint"

cd "$mntpoint"

vndkfile="/system/etc/init/vndk.rc"

sed -i '/on property:sys\.boot_completed=1/a\'\
'    exec u:r:phhsu_daemon:s0 root -- /system/bin/chmod 644 /sys/class/power_supply/battery/charge_control_limit\n'\
'    exec u:r:phhsu_daemon:s0 root -- /system/bin/service call SurfaceFlinger 1008 i32 1\n'\
'    start a9srv' \
".$vndkfile"

printf '\nservice a9srv /system/bin/a9srv\n    disabled\n' >> ".$vndkfile"

cp "$a9srvbin" "./system/bin/a9srv"

setfattr -n security.selinux -v "u:object_r:phhsu_exec:s0" "./system/bin/a9srv"
setfattr -n security.selinux -v "u:object_r:system_file:s0" ".$vndkfile"

echo "Please make further modifications as needed."
echo "When you leave this shell, the process will continue:"
bash

cd -

umount "$mntpoint"

e2fsck -y -f "$image"
resize2fs -M "$image"

lpmake -F -d 10737418240 -m 65536 -s 2 -o "$super" \
-g main_a:10737352704 \
-g main_b:65536 \
-p product_a:none:0:main_a \
-p system_a:none:"$(stat -c'%s' "$image")":main_a \
-p system_ext_a:none:$((512*1099776)):main_a \
-p vendor_a:none:$((512*2496584)):main_a \
-p product_b:none:0:main_b \
-p system_b:none:0:main_b \
-p system_ext_b:none:0:main_b \
-p vendor_b:none:0:main_b \
-i system_ext_a="$systemext" \
-i vendor_a="$vendor" \
-i system_a="$image"

adb reboot fastboot
fastboot flash super "$super"
