// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract ItmoCourseGrades {
    address public professor;

    struct Grades {
        uint256[8] marks;
        bool exists;
    }

    mapping(address => Grades) public grades;
    mapping(address => uint256) public finalGrade;

    modifier onlyProfessor() {
        require(msg.sender == professor, "Only professor");
        _;
    }

    constructor() {
        professor = msg.sender;
    }

    function setGrades(address student, uint256[8] calldata marks)
        external
        onlyProfessor
    {
        grades[student] = Grades({marks: marks, exists: true});
    }

    function computeIntermediate(address student)
        public
        view
        returns (uint256)
    {
        Grades storage g = grades[student];
        require(g.exists, "No grades for this student");

        uint256 ha1 = g.marks[0];
        uint256 ha2 = g.marks[1];
        uint256 ha3 = g.marks[2];
        uint256 ha4 = g.marks[3];
        uint256 ha5 = g.marks[4];
        uint256 ha6 = g.marks[5];
        uint256 examTerm1 = g.marks[6];

        uint256 sumHa1Ha2 = ha1 + ha2;
        uint256 twoTerm = examTerm1 * 2;
        uint256 maxPart = sumHa1Ha2;
        if (twoTerm > maxPart) {
            maxPart = twoTerm;
        }

        uint256 X = maxPart + ha3 + ha4 + ha5 + ha6;

        uint256 intermediate = (X + 3) / 6;
        if (intermediate > 10) {
            intermediate = 10;
        }

        return intermediate;
    }

    function computeFinalGrade(address student)
        external
        onlyProfessor
        returns (uint256)
    {
        Grades storage g = grades[student];
        require(g.exists, "No grades for this student");

        uint256 intermediate = computeIntermediate(student);
        uint256 examFinal = g.marks[7];

        uint256 result;

        if (examFinal > 0) {
            uint256 y = 2 * intermediate + 3 * examFinal;

            uint256 weighted = (y + 2) / 5;
            if (weighted > 10) {
                weighted = 10;
            }
            result = weighted;
        } else {
            if (intermediate >= 6) {
                result = intermediate;
            } else {
                result = 0;
            }
        }

        finalGrade[student] = result;
        return result;
    }
}
