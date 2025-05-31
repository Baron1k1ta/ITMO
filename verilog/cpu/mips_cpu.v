`include "util.v"

module mips_cpu(clk, pc, pc_new, instruction_memory_a, instruction_memory_rd, data_memory_a, data_memory_rd, data_memory_we, data_memory_wd,
                register_a1, register_a2, register_a3, register_we3, register_wd3, register_rd1, register_rd2);
  // сигнал синхронизации
  input clk;
  // текущее значение регистра PC
  inout [31:0] pc;
  // новое значение регистра PC (адрес следующей команды)
  output [31:0] pc_new;
  // we для памяти данных
  output data_memory_we;
  // адреса памяти и данные для записи памяти данных
  output [31:0] instruction_memory_a, data_memory_a, data_memory_wd;
  // данные, полученные в результате чтения из памяти
  inout [31:0] instruction_memory_rd, data_memory_rd;
  // we3 для регистрового файла
  output register_we3;
  // номера регистров
  output [4:0] register_a1, register_a2, register_a3;
  // данные для записи в регистровый файл
  output [31:0] register_wd3;
  // данные, полученные в результате чтения из регистрового файла
  inout [31:0] register_rd1, register_rd2;

  wire [5:0] opencode = instruction_memory_rd[31:26];
  wire [5:0] funct = instruction_memory_rd[5:0];
  wire [31:0] const;
  sign_extend sign_extend0(instruction_memory_rd[15:0], const);
  wire [31:0] j;
  sign26 sign26_0(instruction_memory_rd[25:0], j);
  assign instruction_memory_a = pc;


  wire memReg, memWrite, branch, ALUSrc, regDst, regWrite, jump, jal, jr;
  wire [2:0] ALUControl;
  unit unit0(opencode, funct, memReg, memWrite,regWrite,regDst,ALUSrc,branch,jump, jal, jr,ALUControl);
  
  assign register_a1 = instruction_memory_rd[25:21];
  assign register_a2 = instruction_memory_rd[20:16];
  wire [4:0] tempRegWrite;
  mux2_5 writeAdress1(instruction_memory_rd[20:16], instruction_memory_rd[15:11], regDst, tempRegWrite);
  mux2_5 writeAdress2(tempRegWrite, 5'b11111, jal, register_a3);

  wire [31:0] srcB;
  mux2_32 mux2_32_0(register_rd2, const, ALUSrc, srcB);

  wire [31:0] res;
  wire zero;
  alu alu0(register_rd1, srcB, ALUControl, res, zero);


  assign register_we3 = regWrite;
  assign data_memory_a = res;
  assign data_memory_wd = register_rd2;
  assign data_memory_we = memWrite;
  wire [31:0] temp;
  mux2_32 mux2_32_1(res, data_memory_rd, memReg, temp);
  mux2_32 mux2_32_2(temp, pc0, jal, register_wd3);


  wire [31:0] pc0;
  wire [31:0] pc1;
  wire [31:0] const2;
  shl_2 shl_2_0(const, const2);
  wire p1,p2;
  alu al2(pc, 4, 3'b010, pc0, p1);
  alu al3(pc0, const2, 3'b010, pc1, p2);

  reg PCSrc;
  always @*
    begin
      case (instruction_memory_rd[31:26])
        6'b000100: PCSrc = zero & branch;
        6'b000101: PCSrc = ! zero;
        default: PCSrc = ! zero & branch;
      endcase
    end

  wire [31:0] tempPc;
  mux2_32 choosePC(pc0, pc1, PCSrc, tempPc);
  wire [31:0] aaa;
  shl_2 shl_2_1(j, aaa);
  wire [31:0] tempPc2;
  mux2_32 choosePC1(tempPc, aaa, jump, tempPc2);
  mux2_32 choosePC2(tempPc2, register_rd1, jr, pc_new);
  
endmodule