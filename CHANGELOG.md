This release was long overdue, and for that I apologize. In other news, if you have not been aware we are currently working on a port to 1.21, along with several new or improved features. Unfortunately that does partially take the blame for the delayed-ness of this release.

### Changes

- The way nutrition affects health is slightly adjusted. Essentially, losing hunger will now cause the effect of nutrition loss, and eating food will always restore nutrition (and health). This in practice, means you will be at a slightly lower health when you are not at full hunger than you would've been before. High nutrition health bonus received a small buff to compensate, as it will be harder to stay at maximum nutrition.
- Update localizations for `zh_cn`, `zn_tw`, `zh_hk` (#2798), `ru_ru` (#2774), and `uk_ua` (#2760)

### Fixes

- Fix network compatibility with vanilla servers for use in proxies (#2753)
- Fix food combining recipe creating unwanted container items (#2788)
- Fix empty composter having a solid shape and not allowing players to sit inside it
- Fix an edge case where instant-mining a snow pile would delete the block underneath as well (#2759)
- Fix ice piles voiding blocks above when melting (#2780)
- Fix dried seaweed not turning into soda ash when heated
- Fix the direction of a windmill not getting saved when the last windmill blade was removed
- Fix placement issues with windmill blades (#2802)
- Fix misaligned tooltips when using TFC's patchouli components in custom page layouts (#2805)