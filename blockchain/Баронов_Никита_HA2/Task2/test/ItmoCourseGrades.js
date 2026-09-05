const { expect } = require("chai");
const { ethers } = require("hardhat");

function roundDiv(x, y) {
  return Math.floor((x + Math.floor(y / 2)) / y);
}

function computeExpectedFinal(
  ha1,
  ha2,
  ha3,
  ha4,
  ha5,
  ha6,
  examTerm1,
  examFinal
) {
  const maxPart = Math.max(ha1 + ha2, 2 * examTerm1);
  const X = maxPart + ha3 + ha4 + ha5 + ha6;
  let intermediate = roundDiv(X, 6);
  if (intermediate > 10) intermediate = 10;

  let finalGrade;
  if (examFinal > 0) {
    const y = 2 * intermediate + 3 * examFinal;
    let weighted = roundDiv(y, 5);
    if (weighted > 10) weighted = 10;
    finalGrade = weighted;
  } else {
    finalGrade = intermediate >= 6 ? intermediate : 0;
  }
  return finalGrade;
}

describe("ItmoCourseGrades", function () {
  let Grades, grades, professor, student1, student2, outsider;

  beforeEach(async function () {
    [professor, student1, student2, outsider] = await ethers.getSigners();
    Grades = await ethers.getContractFactory("ItmoCourseGrades");
    grades = await Grades.connect(professor).deploy();
    await grades.waitForDeployment();
  });

  it("1) Non-zero ExamFinal, branch max(HA1+HA2, 2*ExamTerm1) = HA1+HA2", async function () {
    const comps = [8, 7, 6, 7, 5, 6, 6, 9];
    await grades.connect(professor).setGrades(student1.address, comps);

    await grades.connect(professor).computeFinalGrade(student1.address);

    const expected = computeExpectedFinal(...comps);
    const stored = await grades.finalGrade(student1.address);

    expect(stored).to.equal(BigInt(expected));
  });

  it("2) Non-zero ExamFinal, branch max(...) = 2 * ExamTerm1", async function () {
    const comps = [3, 2, 6, 6, 6, 6, 5, 8];
    await grades.connect(professor).setGrades(student1.address, comps);

    await grades.connect(professor).computeFinalGrade(student1.address);

    const expected = computeExpectedFinal(...comps);
    const stored = await grades.finalGrade(student1.address);

    expect(stored).to.equal(BigInt(expected));
  });

  it("3) ExamFinal = 0 and Intermediate >= 6 → final = Intermediate", async function () {
    const comps = [8, 8, 6, 5, 7, 6, 5, 0];
    await grades.connect(professor).setGrades(student1.address, comps);

    await grades.connect(professor).computeFinalGrade(student1.address);

    const expected = computeExpectedFinal(...comps);
    const stored = await grades.finalGrade(student1.address);

    expect(expected).to.be.gte(6);
    expect(stored).to.equal(BigInt(expected));
  });

  it("4) ExamFinal = 0 and Intermediate < 6 → final = 0", async function () {
    const comps = [5, 4, 4, 4, 4, 4, 3, 0];
    await grades.connect(professor).setGrades(student1.address, comps);

    await grades.connect(professor).computeFinalGrade(student1.address);

    const expected = computeExpectedFinal(...comps);
    const stored = await grades.finalGrade(student1.address);

    expect(expected).to.be.lt(6);
    expect(stored).to.equal(0n);
  });

  it("5) Only professor can set grades and compute final grade", async function () {
    const comps = [10, 10, 10, 10, 10, 10, 10, 10];

    await expect(
      grades.connect(outsider).setGrades(student1.address, comps)
    ).to.be.revertedWith("Only professor");

    await grades.connect(professor).setGrades(student1.address, comps);

    await expect(
      grades.connect(outsider).computeFinalGrade(student1.address)
    ).to.be.revertedWith("Only professor");
  });
});
