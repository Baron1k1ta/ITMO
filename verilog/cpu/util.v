// модуль, который реализует расширенение
// 16-битной знаковой константы до 32-битной
module sign_extend(in, out);
  input [15:0] in;
  output [31:0] out;

  assign out = {{16{in[15]}}, in};
endmodule

// модуль, который реализует побитовый сдвиг числа
// влево на 2 бита
module shl_2(in, out);
  input [31:0] in;
  output [31:0] out;

  assign out = {in[29:0], 2'b00};
endmodule

// 32 битный сумматор
module adder(a, b, out);
  input [31:0] a, b;
  output [31:0] out;

  assign out = a + b;
endmodule

// 32-битный мультиплексор
module mux2_32(d0, d1, a, out);
  input [31:0] d0, d1;
  input a;
  output [31:0] out;
  assign out = a ? d1 : d0;
endmodule

// 5 - битный мультиплексор
module mux2_5(d0, d1, a, out);
  input [4:0] d0, d1;
  input a;
  output [4:0] out;
  assign out = a ? d1 : d0;
endmodule


module alu(
  input wire [31:0] a, b,
  input [2:0] in,
  output reg [31:0] out,
  output reg zero
);

  always @*
  begin
    case (in)
      3'b000: out = a & b;
      3'b001: out = a | b;
      3'b010: out = a + b;
      3'b110: out = a - b;
      3'b111: begin
        if ((a < b) && (a[31] >= b[31])) begin
          out = {1'b0, 31'b0};
          out[0] = 1;
        end else if ((a > b) && (a[31] > b[31])) begin
          out = {1'b0, 31'b0};
          out[0] = 1;
        end else begin
          out = {1'b0, 31'b0};
        end
      end
      default: out = {1'b0, 31'b0};
    endcase
  end

  always @*
  begin
    zero = (out == 0);
  end

endmodule

module unit(opencode,funct,memReg,memWrite,regWrite,regDst,ALUSrc,branch,j,jal,jr,ALUControl);

input wire [5:0] opencode, funct;
output reg memReg,memWrite,regWrite,regDst,ALUSrc,branch,j,jal,jr;
output reg [2:0] ALUControl;

reg [1:0] ALUOp;


always @* begin

case (opencode)
  6'b000000: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 1;
    regDst = 1;
    ALUSrc = 0;
    branch = 0;
    ALUOp = 2'b10;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b000010: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 0;
    regDst = 0;
    ALUSrc = 0;
    branch = 0;
    ALUOp = 2'b10;
    j = 1;
    jal = 0;
    jr = 0;
    end
    6'b000011: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 1;
    regDst = 0;
    ALUSrc = 0;
    branch = 0;
    ALUOp = 2'b10;
    j = 1;
    jal = 1;
    jr = 0;
    end
    6'b000100: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 0;
    regDst = 0;
    ALUSrc = 0;
    branch = 1;
    ALUOp = 2'b01;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b000101: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 0;
    regDst = 0;
    ALUSrc = 0;
    branch = 1;
    ALUOp = 2'b01;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b001000: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 1;
    regDst = 0;
    ALUSrc = 1;
    branch = 0;
    ALUOp = 2'b00;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b001100: begin
    memReg = 0;
    memWrite = 0;
    regWrite = 1;
    regDst = 0;
    ALUSrc = 1;
    branch = 0;
    ALUOp = 2'b11;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b100011: begin
    memReg = 1;
    memWrite = 0;
    regWrite = 1;
    regDst = 0;
    ALUSrc = 1;
    branch = 0;
    ALUOp = 2'b00;
    j = 0;
    jal = 0;
    jr = 0;
    end
    6'b101011: begin
    memReg = 0;
    memWrite = 1;
    regWrite = 0;
    regDst = 0;
    ALUSrc = 1;
    branch = 0;
    ALUOp = 2'b00;
    j = 0;
    jal = 0;
    jr = 0;
    end
endcase
end

always @*
begin
  case (ALUOp)
    2'b00: ALUControl = 3'b010;
    2'b01: ALUControl = 3'b110;
    2'b10: begin
      case (funct)
        6'b100100: ALUControl = 3'b000;
        6'b100101: ALUControl = 3'b001;
        6'b100000: ALUControl = 3'b010;
        6'b100010: ALUControl = 3'b110;
        6'b101010: ALUControl = 3'b111;
        default: ALUControl = 3'b000;
      endcase
    end
    default: ALUControl = 3'b000;
  endcase
end

endmodule


module sign26(in, out);
  input [25:0] in;
  output [31:0] out;

  assign out = {6'b000000, in};
endmodule