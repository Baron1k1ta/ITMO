import hre from "hardhat";

async function main() {
  const connection = await hre.network.connect();
  const viem = connection.viem;

  // Один аккаунт — тот, чей приватный ключ в SEPOLIA_PRIVATE_KEY
  const [deployer] = await viem.getWalletClients();
  const publicClient = await viem.getPublicClient();

  const contractAddress = "0xf90C1c299d1f644d06b1F8BB93D179B00ff59775";

  // ВАЖНО: передаём адрес как строку, а не объект { address: ... }
  const adNft = await viem.getContractAt(
    "Web3MarketingAdNFT",
    contractAddress as `0x${string}`
  );

  console.log("Deployer (admin & advertiser):", deployer.account.address);

  // 1) Назначаем самого себя рекламодателем
  console.log("Adding advertiser role (self)...");
  const addHash = await adNft.write.addAdvertiser(
    [deployer.account.address],
    { account: deployer.account.address }
  );
  await publicClient.waitForTransactionReceipt({ hash: addHash });
  console.log("Advertiser role added");

  // 2) Создаём кампанию с твоей картинкой как URI
  const uri =
    "https://turquoise-advisory-whippet-692.mypinata.cloud/ipfs/bafkreid2n7y2tryaue7zfoctkiqzqxurz575omek6zvmaru5hhtdxboepy";

  console.log("Creating campaign with URI:", uri);
  const createHash = await adNft.write.createCampaign([uri], {
    account: deployer.account.address,
  });
  await publicClient.waitForTransactionReceipt({ hash: createHash });

  const campaignId = await adNft.read.lastCampaignId();
  console.log("Campaign created with id:", campaignId.toString());

  // 3) Отправляем рекламный NFT пользователю
  const recipient = "0xb3500576da2f9ae0ee81af3fe9dafed7c5377d43";

  console.log("Sending ad NFT to:", recipient);
  const sendHash = await adNft.write.sendAdToMany(
    [campaignId, [recipient]],
    { account: deployer.account.address }
  );
  await publicClient.waitForTransactionReceipt({ hash: sendHash });
  console.log("Ad NFT sent");

  // 4) Проверяем баланс и URI
  const balance = await adNft.read.balanceOf([recipient]);
  console.log("Recipient balance:", balance.toString());

  if (balance > 0n) {
    const uri0 = await adNft.read.tokenURI([1n]);
    console.log("Token 1 URI:", uri0);
  }
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
