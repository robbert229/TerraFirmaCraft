Another small crash fix of the "this worked in dev" variety. Unfortunately, this arose because our compiler decided to do something different from what it has been doing for a ~year, forcing us to rename a method. This release may break addons as a result, although it should be a fairly straightforward update.

### Changes

- Fix crash due to `IFluidLoggable.m_5888_` (#2812)
- Fix baby horses only inheriting speed from one parent (#2813)