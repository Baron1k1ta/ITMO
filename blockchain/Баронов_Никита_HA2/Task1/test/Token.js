const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("Token", function () {
  let token, owner, user1, user2;
  const initialSupply = ethers.parseUnits("100", 18);
  const transferAmount = ethers.parseUnits("20", 18);

  beforeEach(async function () {
    [owner, user1, user2] = await ethers.getSigners();
    const Token = await ethers.getContractFactory("Token");
    token = await Token.deploy(initialSupply);
    await token.waitForDeployment();
  });

  it("правильно распределяет балансы после двух переводов", async function () {
    await token.transfer(user1.address, transferAmount);
    expect(await token.balanceOf(user1.address)).to.equal(transferAmount);

    const expectedAfterFirst = initialSupply - transferAmount;
    expect(await token.balanceOf(owner.address)).to.equal(expectedAfterFirst);

    await token.transfer(user2.address, transferAmount);
    expect(await token.balanceOf(user2.address)).to.equal(transferAmount);

    const expectedOwnerBalance = initialSupply - transferAmount * 2n;
    expect(await token.balanceOf(owner.address)).to.equal(expectedOwnerBalance);
  });
});
