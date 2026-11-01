#!/usr/bin/env python3
"""Switch between the two launchers while retaining unrelated accessibility services."""
import subprocess
import sys

SERVICES={name:f'{pkg}/{pkg}.HomeRedirectService' for name,pkg in
          (('outfit','com.cleantv.outfit'),('original','com.homestead.launcher'))}

def service_list(current, choice):
    kept=[s for s in current.strip().split(':') if s and s!='null' and
          s.split('/')[0] not in ('com.cleantv.outfit','com.homestead.launcher')]
    return ':'.join(dict.fromkeys(kept+[SERVICES[choice]]))

if __name__=='__main__':
    tv,choice=sys.argv[1:]
    if choice not in SERVICES: raise SystemExit('Choose outfit or original')
    cmd=['adb','-s',tv,'shell','settings']
    current=subprocess.check_output(cmd+['get','secure','enabled_accessibility_services'],text=True)
    updated=service_list(current,choice)
    # ADB shell joins arguments: quote the setting value rather than treating it as shell code.
    import shlex
    subprocess.run(cmd+['put','secure','enabled_accessibility_services',shlex.quote(updated)],check=True)
    subprocess.run(cmd+['put','secure','accessibility_enabled','1'],check=True)
