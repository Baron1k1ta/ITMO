import { describe, it } from "node:test";
import { expect } from "chai";
import hre from "hardhat";

const uri = "ipfs://campaign1/metadata.json";

async function deployAdNft() {
  const connection = await hre.network.connect();
  const viem = connection.viem;
  const [admin, advertiser, user1, user2, user3] =
    await viem.getWalletClients();
  const publicClient = await viem.getPublicClient();

  const adNft = await viem.deployContract("Web3MarketingAdNFT", [], {
    walletClient: admin,
  });

  // admin adds advertiser
  const addHash = await adNft.write.addAdvertiser([advertiser.account.address], {
    account: admin.account.address,
  });
  await publicClient.waitForTransactionReceipt({ hash: addHash });

  return { adNft, admin, advertiser, user1, user2, user3, publicClient, viem };
}

async function createCampaign(adNft: any, advertiser: any, publicClient: any) {
  const createHash = await adNft.write.createCampaign([uri], {
    account: advertiser.account.address,
  });
  await publicClient.waitForTransactionReceipt({ hash: createHash });
  return await adNft.read.lastCampaignId();
}

describe("Web3MarketingAdNFT", () => {
  it("mints ads to recipients and exposes tokenURI", async () => {
    const { adNft, advertiser, user1, user2, publicClient } = await deployAdNft();
    const campaignId = await createCampaign(adNft, advertiser, publicClient);

    const sendHash = await adNft.write.sendAdToMany(
      [campaignId, [user1.account.address, user2.account.address]],
      { account: advertiser.account.address }
    );
    await publicClient.waitForTransactionReceipt({ hash: sendHash });

    expect(await adNft.read.balanceOf([user1.account.address])).to.equal(1n);
    expect(await adNft.read.balanceOf([user2.account.address])).to.equal(1n);
    expect(await adNft.read.tokenURI([1n])).to.equal(uri);
    expect(await adNft.read.tokenURI([2n])).to.equal(uri);
  });

  it("respects global opt-out and per-advertiser block", async () => {
    const { adNft, advertiser, user1, user2, publicClient } = await deployAdNft();
    const campaignId = await createCampaign(adNft, advertiser, publicClient);

    // user1 opts out globally
    const optHash = await adNft.write.setGlobalOptOut([true], {
      account: user1.account.address,
    });
    await publicClient.waitForTransactionReceipt({ hash: optHash });

    // user2 blocks advertiser
    const blockHash = await adNft.write.setAdvertiserBlocked(
      [advertiser.account.address, true],
      { account: user2.account.address }
    );
    await publicClient.waitForTransactionReceipt({ hash: blockHash });

    const sendHash = await adNft.write.sendAdToMany(
      [campaignId, [user1.account.address, user2.account.address]],
      { account: advertiser.account.address }
    );
    await publicClient.waitForTransactionReceipt({ hash: sendHash });

    expect(await adNft.read.balanceOf([user1.account.address])).to.equal(0n);
    expect(await adNft.read.balanceOf([user2.account.address])).to.equal(0n);
  });

  it("blocks transfers (soulbound)", async () => {
    const { adNft, advertiser, user1, user2, publicClient } = await deployAdNft();
    const campaignId = await createCampaign(adNft, advertiser, publicClient);

    const sendHash = await adNft.write.sendAdToMany(
      [campaignId, [user1.account.address]],
      { account: advertiser.account.address }
    );
    await publicClient.waitForTransactionReceipt({ hash: sendHash });

    let caught: unknown;
    try {
      await adNft.write.transferFrom(
        [user1.account.address, user2.account.address, 1n],
        { account: user1.account.address }
      );
    } catch (err) {
      caught = err;
    }
    expect(caught).to.be.instanceOf(Error);
    expect(String((caught as Error).message)).to.include("Transfers disabled");
  });
});
