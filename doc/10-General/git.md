# This is the one true source!

## Code of Commit:
We use fast-forward merging together with squashing for a clean commit history

## Branches
We have 3 Everlasting Branches: main, documentation and devel.
Documentation and devel are also known as "Big2"
These are to be respected and treated well!

### main
main is our most important branch, thats why we dont commit to it directly!
In order to add to main you need to merge one of the Big2 to main.

### documentation
documentation is for everything non code related like adding readmes, configs or other things.
documentation can be worked on directly

### devel
Our busiest branch: devel, working on it has a few rules that have to be followed!

#### fixing
For fixes and refactors you can use devel directly.
If these are minor use:
```sh
git commit --fixup=#UUID
```
and rebase accordingly with:
```sh
git rebase --interactive --autosquash #UUID
```

#### features
If you want to add features to devel create a new branch and merge it with devel afterwards.
How to:


## Git Knowledegbase
### check commit history
This is also how you get the commit #UUIDs for other commands:
```sh
git log --oneline --decorate --graph
```

### how to get new stuff:

On main do this:
```sh
git fetch origin
```
Then do these:
```sh
git checkout Your-Feature-Branch
```
```sh
git rebase origin/main
```
After you have solved all conflicts do these:
```sh
git rebase --continue
```
```sh
git push -f origin your-feature-branch
```
