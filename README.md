# Mechanism Library

The purpose of this library is to collect common domain and market mechanism implementations in the most general way possible.
For example, you can create a set of bidders with XOR valuation on a set of goods (which already forms a simple standard-domain),
and then do various things with this domain, e.g.:

* Run Winner Determination Problems (WDPs) to find the optimal allocation
* Collect sets of bids from the bidders according to a certain strategy (e.g., truthful)
  * Based on this bids, find the resulting allocation with bid-based WDPs, or try out different payment rules (e.g., VCG, or variations of CCG)
* Ask bidders for their value for a certain bundle
* Ask bidders for the optimal bundle given certain prices
* Run instances of complete auction formats (incl. multi-round auctions like the Combinatorial Clock Auction) in this domain

Most importantly, the library should serve as a dependency in your project.
Thus, you will most likely create your own classes that implement the corresponding interfaces in the `core/` package.
These interfaces are well-documented and serve as a good starting point to use this library.
It's also worth checking the tests to see how some of the above functionality is used.

This library was developed at the [University of Zurich](https://www.uzh.ch/) in collaboration with [Boston University](https://www.bu.edu/).
