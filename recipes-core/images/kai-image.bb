SUMMARY = "Image kai pour rpi4-64 (Flutter + FFI + gRPC + Wifi"
LICENSE = "MIT"

# Base : core-image-minimal
require recipes-core/images/core-image-minimal.bb

# Services et formats d’image
IMAGE_FEATURES += " ssh-server-dropbear "
IMAGE_FSTYPES += " rpi-sdimg "
BOOT_SPACE = "131072"

# Paquets que tu avais dans local.conf
IMAGE_INSTALL:append = " \
  flutter-pi flutter-ffi-grpc \
  flutter-samples-date-planner \
  wifi-config wpa-supplicant iw wireless-regdb-static \
  linux-firmware-bcm43455 kernel-module-brcmfmac \
  kernel-modules \
  libinput libinput-bin evtest \
  ldd \
  brcmfmac-autoload \
  htop \
"

