#!/bin/bash -e

# check arguments
if [ $# -lt 3 ]; then
  echo "Usage: $0 <vm-name> <bin-dir> <log-dir> [<vdi-file>]"
  echo
  echo " vm-name:  The name of the virtual machine that will be started/created"
  echo " bin-dir:  The directory containing the sandboxd binary"
  echo " log-dir:  The directory where the sandbox will write the logs"
  echo " vdi-file: The file of the hard disk for the virtual machine"
  exit
fi
if [ $# -ge 4 ]; then
  SANDBOX_VDI="$4"
fi

# if the vdi is already registered, unregister it because we will create a new clone
vdi="/var/cache/sandbox-$1.vdi"
if [ "$(VBoxManage list hdds | grep "$vdi")" != "" ]; then
  echo "- Unregistering the old hard disk ..."
  VBoxManage closemedium disk "$vdi"
fi

# download and clone the hard disk image
if [ ! -e "$SANDBOX_VDI" ]; then
  SANDBOX_VDI=/var/cache/sandbox.vdi
  if [ ! -e "$SANDBOX_VDI" ]; then
    echo "- Downloading the hard disk image ..."
    wget -O $SANDBOX_VDI 'https://www.dropbox.com/s/shtmc1uue4ql6gh/sandbox.vdi?dl=0'
  fi
fi
test ! -e "$vdi" || rm -f "$vdi"
echo "- Cloning the hard disk ..."
VBoxManage clonehd "$SANDBOX_VDI" "$vdi"

# make sure the host only interface exists
if [ "$(VBoxManage list hostonlyifs | grep vboxnet0)" == "" ]; then
  echo "- Creating a Host-Only network ..."
  VBoxManage hostonlyif create
  echo "- Installing a DHCP Server ..."
  VBoxManage dhcpserver add --ifname vboxnet0 --enable \
      --ip 192.168.56.100 --netmask 255.255.255.0 \
      --lowerip 192.168.56.101 --upperip 192.168.56.199
fi

# if the vm exists, go back to the installation snapshot
if [ "$(VBoxManage list vms | grep "$1")" != "" ]; then
  echo "- Reseting VM to the snapshot ..."
  VBoxManage snapshot "$1" restore 'new'
  
# else create the vm
else
  export $(cat defaults.prop)
  echo "- Creating VM ..."
  VBoxManage createvm --name "$1" --ostype $defOs --register
  echo "- Settings up VM ..."
  VBoxManage modifyvm "$1" --memory $defMem --cpus $defCpus --cpuexecutioncap $defCpuExecutionCap --pae on \
      --boot1 disk --boot2 none --boot3 none --boot4 none \
      --rtcuseutc $defUtc --usb $defUsb --audio none \
      --nic1 hostonly --hostonlyadapter1 vboxnet0 --macaddress1 auto
  echo "- Adding a sata controler ..."
  VBoxManage storagectl "$1" --name SATA --add sata --portcount 1 --bootable on
  echo "- Adding the hard disk ..."
  VBoxManage storageattach "$1" --storagectl SATA --port 1 --medium "$vdi" --type hdd
  echo "- Adding the shared folders ..."
  VBoxManage sharedfolder add "$1" --name "Sandbox" --hostpath "$(realpath "$2")" --readonly
  VBoxManage sharedfolder add "$1" --name "logs" --hostpath "$(realpath "$3")"
  echo "- Creating a snapshot ..."
  VBoxManage snapshot "$1" take 'new' --description 'snapshot just after the startVM.sh script created the vm'
  
fi

# finally, start the vm
echo "- Starting the VM ..."
VBoxManage startvm "$1" --type headless
echo "- Done"
