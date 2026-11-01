#!/usr/bin/env python3
"""Fail if customization spreads into another upstream-owned file."""
from pathlib import Path
import subprocess
root=Path(__file__).resolve().parents[1]
allowed={'.gitignore',*[f'src/com/homestead/launcher/{name}.java' for name in
    ('Backgrounds','HomeGrid','Panel','SettingsPanel','DebloatPanel')]}
upstream=subprocess.check_output(['git','ls-tree','-r','--name-only','upstream/main'],cwd=root,text=True).splitlines()
changed=[]
for name in upstream:
    expected=subprocess.check_output(['git','show',f'upstream/main:{name}'],cwd=root)
    path=root/name
    if not path.exists() or path.read_bytes()!=expected: changed.append(name)
unexpected=set(changed)-allowed
assert not unexpected, f'New upstream edit needs architectural review: {sorted(unexpected)}'
print('PASS: upstream differences confined to '+', '.join(changed))
