# Web3 Marketing Ad NFT (Hardhat v3 + viem)

This project demonstrates “cold advertising via NFT sending” with opt-out and per-advertiser blocks. It uses Hardhat v3, the viem toolbox, and an Ignition module for deployment.

## Prereqs
- Node.js ≥ 22.10 (Hardhat v3 requirement)
- `npm install` in this folder to pull Hardhat, OZ, etc.

## Contracts
- `contracts/AdNFT.sol` — ERC721-based ad dropper with roles:
  - Admin grants/removes advertisers.
  - Advertiser creates campaigns and airdrops NFTs (`sendAdToMany`).
  - Users can opt out globally or block a specific advertiser.
  - Soulbound: transfers are disabled; metadata shared per campaign.

## Commands
- Compile: `npm run compile`
- Tests: `npm test` (runs viem-based tests in `test/AdNFT.test.ts`)
- Local node (L1 sim): `npx hardhat node --network hardhatMainnet`

## Ignition deploy
- Local simulated chain:  
  `npx hardhat ignition deploy ignition/modules/AdNFT.ts --network hardhatMainnet`
- Sepolia (needs RPC + privkey):  
  `SEPOLIA_RPC_URL=... SEPOLIA_PRIVATE_KEY=... npx hardhat ignition deploy ignition/modules/AdNFT.ts --network sepolia`

## Manual flow (scripts)
After deploy, use viem console or add scripts to:
1) `addAdvertiser(advertiser)`
2) `createCampaign(uri)`
3) `sendAdToMany(campaignId, [recipients])`
Users: `setGlobalOptOut(true)` or `setAdvertiserBlocked(advertiser, true)` to stop ads.
