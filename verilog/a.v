module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции
  output [3:0] res; // Результат
  wire [3:0] res0,res1,res2,res3,res4,res5,res6;

  all_and all_and0 (a,b,res0);
  not_all_and not_all_and0(a,b,res1);
  all_or all_or0(a,b,res2);
  not_all_or not_all_or0(a,b,res3);
  summ summ0(a,b,res4);
  substance substance0 (a,b,res5);
  slt slt0 (a,b,res6);

  a0 alu0 (control,res_alu0);
  a1 alu1 (control,res_alu1);
  a2 alu2 (control,res_alu2);
  a3 alu3 (control,res_alu3);
  a4 alu4 (control,res_alu4);
  a5 alu5 (control,res_alu5);
  a6 alu6 (control,res_alu6);

  nmos nmos00(res[0],res0[0],res_alu0);
  nmos nmos01(res[1],res0[1],res_alu0);
  nmos nmos02(res[2],res0[2],res_alu0);
  nmos nmos03(res[3],res0[3],res_alu0);

  nmos nmos10(res[0],res1[0],res_alu1);
  nmos nmos11(res[1],res1[1],res_alu1);
  nmos nmos12(res[2],res1[2],res_alu1);
  nmos nmos13(res[3],res1[3],res_alu1);
  
  nmos nmos20(res[0],res2[0],res_alu2);
  nmos nmos21(res[1],res2[1],res_alu2);
  nmos nmos22(res[2],res2[2],res_alu2);
  nmos nmos23(res[3],res2[3],res_alu2);

  nmos nmos30(res[0],res3[0],res_alu3);
  nmos nmos31(res[1],res3[1],res_alu3);
  nmos nmos32(res[2],res3[2],res_alu3);
  nmos nmos33(res[3],res3[3],res_alu3);

  nmos nmos40(res[0],res4[0],res_alu4);
  nmos nmos41(res[1],res4[1],res_alu4);
  nmos nmos42(res[2],res4[2],res_alu4);
  nmos nmos43(res[3],res4[3],res_alu4);

  nmos nmos50(res[0],res5[0],res_alu5);
  nmos nmos51(res[1],res5[1],res_alu5);
  nmos nmos52(res[2],res5[2],res_alu5);
  nmos nmos53(res[3],res5[3],res_alu5);

  nmos nmos60(res[0],res6[0],res_alu6);
  nmos nmos61(res[1],res6[1],res_alu6);
  nmos nmos62(res[2],res6[2],res_alu6);
  nmos nmos63(res[3],res6[3],res_alu6);
  // TODO: implementation
endmodule

module a0(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b000;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a1(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b001;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a2(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b010;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a3(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b011;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a4(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b100;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a5(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b101;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a6(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b110;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module a7(control,res);
  input [2:0] control;
  output wire res;
  reg [2:0] a = 3'b111;
  xor_gate xor_gate0(control[0],a[0],a0);
  xor_gate xor_gate1(control[1],a[1],a1);
  xor_gate xor_gate2(control[2],a[2],a2);
  or_gate or_gate0(a0,a1,a3);
  or_gate or_gate1(a3,a2,a5);
  not_gate not_gate0(a5,res);
endmodule

module all_and(a,b,out);
  input [3:0] a;
  input [3:0] b;
  output wire [3:0] out;
  and_gate and_gate0(a[0],b[0],out[0]);
  and_gate and_gate1(a[1],b[1],out[1]);
  and_gate and_gate2(a[2],b[2],out[2]);
  and_gate and_gate3(a[3],b[3],out[3]);
endmodule

module not_all_and(a,b,out);
  input [3:0] a;
  input [3:0] b;
  output wire [3:0] out;
  nand_gate nand_gate0(a[0],b[0],out[0]);
  nand_gate nand_gate1(a[1],b[1],out[1]);
  nand_gate nand_gate2(a[2],b[2],out[2]);
  nand_gate nand_gate3(a[3],b[3],out[3]);
endmodule

module all_or(a,b,out);
  input [3:0] a;
  input [3:0] b;
  output wire [3:0] out;
  or_gate or_gate0(a[0],b[0],out[0]);
  or_gate or_gate1(a[1],b[1],out[1]);
  or_gate or_gate2(a[2],b[2],out[2]);
  or_gate or_gate3(a[3],b[3],out[3]);
endmodule

module not_all_or(a,b,out);
  input [3:0] a;
  input [3:0] b;
  output wire [3:0] out;
  nor_gate nor_gate0(a[0],b[0],out[0]);
  nor_gate nor_gate1(a[1],b[1],out[1]);
  nor_gate nor_gate2(a[2],b[2],out[2]);
  nor_gate nor_gate3(a[3],b[3],out[3]);
endmodule

module summer(a, b, transition_in, res, transition_out);
  input a, b, transition_in;
  output wire res, transition_out;
  xor_gate xor_gate0(a, b, x0);
  xor_gate xor_gate1(x0, transition_in, transition_out);
  and_gate and_gate0(a, b, a0);
  and_gate and_gate1(a, transition_in, a1);
  and_gate and_gate2(b, transition_in, a2);
  or_gate or_gate0(a0, a1, a3);
  or_gate or_gate1(a3, a2, res);
endmodule

module summ(a, b, out);
  input [3:0] a, b;
  output[3:0] out;
  summer summer0(a[0], b[0], 1'b0, res0, out[0]);
  summer summer1(a[1], b[1], res0, res1, out[1]);
  summer summer2(a[2], b[2], res1, res2, out[2]);
  summer summer3(a[3], b[3], res2, res3, out[3]);
endmodule

module substance(a, b, out);
  input [3:0] a, b;
  output wire [3:0] out;
  not_gate not_gate0(b[0], n0);
  not_gate not_gate1(b[1], n1);
  not_gate not_gate2(b[2], n2);
  not_gate not_gate3(b[3], n3);  
  summer summer0(a[0], n0, 1'b1, res0, out[0]);
  summer summer1(a[1], n1, res0, res1, out[1]);
  summer summer2(a[2], n2, res1, res2, out[2]);
  summer summer3(a[3], n3, res2, res3, out[3]); 
endmodule

module slt(a,b,out);
  input [3:0] a, b;
  output wire [3:0] out;
  wire s = 1;
  wire [3:0] s0;
  not_gate not_gate1(s,out[1]);
  not_gate not_gate2(s,out[2]);
  not_gate not_gate3(s,out[3]);
  xor_gate xor_gate0(a[3],b[3],x0);
  substance substance0(a,b,s0);
  not_gate not_gate0(x0,x1);
  and_gate and_gate0(x1,s0[3],a0);
  not_gate not_gate4(s0[3],n0);
  and_gate and_gate4(x1,n0,a1);
  supply1 v_dd;
  supply0 gnd;
  nmos nmos0(out[0],v_dd,a0);
  nmos nmos1(out[0],gnd,a1);
  nmos nmos2(out[0],a[3],x0);
endmodule  

module d_latch(clk, d, we, q);
  input clk; // Сигнал синхронизации
  input d; // Бит для записи в ячейку
  input we; // Необходимо ли перезаписать содержимое ячейки

  output reg q; // Сама ячейка
  // Изначально в ячейке хранится 0
  initial begin
    q <= 0;
  end
  // Значение изменяется на переданное на спаде сигнала синхронизации
  always @ (negedge clk) begin
    // Запись происходит при we = 1
    if (we) begin
      q <= d;
    end
  end
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data, we);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл
  input we; // Необходимо ли перезаписать содержимое регистра
  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  wire [3:0] register_ind;
  wire [3:0] reg0;
  wire [3:0] reg1;
  wire [3:0] reg2;
  wire [3:0] reg3;
  demux demux0(we_addr, register_ind);
  register register0(clk, we_data, register_ind[0], reg0);
  register register1(clk, we_data, register_ind[1], reg1);
  register register2(clk, we_data, register_ind[2], reg2);
  register register3(clk, we_data, register_ind[3], reg3);
  mux mux0(reg0, reg1, reg2, reg3, rd_addr, rd_data);
  // TODO: implementation
endmodule

module register (clk, d, we, q);
  input clk;
  input [3:0] d;
  input we;
  output wire [3:0] q;
  d_latch d_latch0(clk, d[0], we, q[0]);
  d_latch d_latch1(clk, d[1], we, q[1]);
  d_latch d_latch2(clk, d[2], we, q[2]);
  d_latch d_latch3(clk, d[3], we, q[3]); 
endmodule

module demux(we_addr, register_ind);
  input [1:0] we_addr;
  output [3:0] register_ind;
  not_gate not_gate0(we_addr[0], n0);
  not_gate not_gate1(we_addr[1], n1);
  and_gate and_gate0(n0, n1, register_ind[0]);
  and_gate and_gate1(we_addr[0], n1, register_ind[1]);
  and_gate and_gate2(n0, we_addr[1], register_ind[2]);
  and_gate and_gate3(we_addr[0], we_addr[1], register_ind[3]);
endmodule

module mux(a0, a1, a2, a3, b, out);
  input wire [3:0] a0, a1, a2, a3;
  input wire [1:0] b;
  output wire [3:0] out;
  wire [1:0] n;
  wire [3:0] res0, res1, res2, res3; 
  not_gate not_gate0(b[0], n[0]);
  not_gate not_gate1(b[1], n[1]);
  and_gate and_gate0(n[0], n[1], and0);
  and_gate and_gate1(and0, a0[0], res0[0]);
  and_gate and_gate2(and0, a0[1], res0[1]);
  and_gate and_gate3(and0, a0[2], res0[2]);
  and_gate and_gate4(and0, a0[3], res0[3]);
  and_gate and_gate5(b[0], n[1], and1);
  and_gate and_gate6(and1, a1[0], res1[0]);
  and_gate and_gate7(and1, a1[1], res1[1]);
  and_gate and_gate8(and1, a1[2], res1[2]);
  and_gate and_gate9(and1, a1[3], res1[3]);
  and_gate and_gate10(b[1], n[0], and2);
  and_gate and_gate11(and2, a2[0], res2[0]);
  and_gate and_gate12(and2, a2[1], res2[1]);
  and_gate and_gate13(and2, a2[2], res2[2]);
  and_gate and_gate14(and2, a2[3], res2[3]);
  and_gate and_gate15(b[1], b[0], and3);
  and_gate and_gate16(and3, a3[0], res3[0]);
  and_gate and_gate17(and3, a3[1], res3[1]);
  and_gate and_gate18(and3, a3[2], res3[2]);
  and_gate and_gate19(and3, a3[3], res3[3]);
  or_gate or_gate0(res0[0], res1[0], or0);
  or_gate or_gate1(or0, res2[0], or1);
  or_gate or_gate2(or1, res3[0], out[0]);
  or_gate or_gate3(res0[1], res1[1], or2);
  or_gate or_gate4(or2, res2[1], or3);
  or_gate or_gate5(or3, res3[1], out[1]);
  or_gate or_gate6(res0[2], res1[2], or4);
  or_gate or_gate7(or4, res2[2], or5);
  or_gate or_gate8(or5, res3[2], out[2]);
  or_gate or_gate9(res0[3], res1[3], or6);
  or_gate or_gate10(or6, res2[3], or7);
  or_gate or_gate11(or7, res3[3], out[3]);
endmodule

module counter(clk, addr, control, immediate, data);
  input clk; // Сигнал синхронизации
  input [1:0] addr; // Номер значения счетчика которое читается или изменяется
  input [3:0] immediate; // Целочисленная константа, на которую увеличивается/уменьшается значение счетчика
  input control; // 0 - операция инкремента, 1 - операция декремента
  wire [2:0] control_alu;
  wire [3:0] alu_out;
  wire [3:0] register_out;
  wire we = 0;
  
  not_gate not_gate0(we,control_alu[2]);
  not_gate not_gate1(control_alu[2],control_alu[1]);
  xor_gate xor_gate0(we,control,control_alu[0]);
  
  register_file register_file0(clk,addr,addr, alu_out, register_out,we );
  alu alu0(register_out,immediate,control_alu, alu_out);

  assign data = register_out;


  output [3:0] data; // Данные из значения под номером addr, подающиеся на выход
  // TODO: implementation
endmodule

module not_gate(a,out);
  input wire a;
  output wire out;
  supply1 v_dd;
  supply0 gnd;
  pmos pmos1(out,v_dd,a);
  nmos nmos1(out,gnd,a);
endmodule

module nand_gate(a,b,out);
    input wire a, b;
    output wire out;
    supply1 v_dd;
    supply0 gnd;
    wire nmos1_out;
    pmos pmos1(out,v_dd,a);
    pmos pmos2(out,v_dd,b);
    nmos nmos1(nmos1_out,gnd,b);
    nmos nmos2(out,nmos1_out,a);
endmodule

module and_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(a, b, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module or_gate(a,b,out);
  input wire a, b;
  output wire out;
  supply1 v_dd;
  supply0 gnd;
  wire pmos1_out;
  nmos nmos1(out,v_dd,a);
  nmos nmos2(out,v_dd,b);
  pmos pmos1(pmos1_out,gnd,b);
  pmos pmos2(out,pmos1_out,a);
endmodule

module nor_gate(a, b, out);
  input wire a, b;
  output wire out;

  wire or_out;

  or_gate or_gate1(a, b, or_out);
  not_gate not_gate1(or_out, out);
endmodule

module xor_gate(a,b,out);
  input wire a, b;
  output wire out;
  wire not_a, not_b;
  wire out1, out2;
  not_gate not_gate1(a,not_a);
  not_gate not_gate2(b,not_b);
  and_gate and_gate1(not_a,b,out1);
  and_gate and_gate2(a,not_b,out2);
  or_gate or_gate1(out1,out2,out);
endmodule























// wire [3:0] current;
//   wire [3:0] register_ind;
//   wire [3:0] reg0 = 4'b0000;
//   wire [3:0] reg1;
//   wire [3:0] reg2;
//   wire [3:0] reg3;
//   wire [3:0] reg4;
//   wire [3:0] plus;
//   wire [3:0] minus;
//   wire [3:0] res;
//   wire a = 0;
//   wire b = 1;
//   demux demux0(addr, register_ind);

//   register register4(clk, reg0, a , reg1);
//   register register5(clk, reg0, a, reg2);
//   register register6(clk, reg0, a, reg3);
//   register register7(clk, reg0, a, reg4);
//   mux mux1(reg1, reg2, reg3, reg4, addr, res);

//   summ summ0(reg0,immediate,plus);
//   substance substance0(reg0,immediate,minus);
  
//   pmos pmos0(res[0],plus[0],control);
//   pmos pmos1(res[1],plus[1],control);
//   pmos pmos2(res[2],plus[2],control);
//   pmos pmos3(res[3],plus[3],control);

//   nmos nmos0(res[0],minus[0],control);
//   nmos nmos1(res[1],minus[1],control);
//   nmos nmos2(res[2],minus[2],control);
//   nmos nmos3(res[3],minus[3],control);


 

//   register register0(clk, res, register_ind[0], reg1);
//   register register1(clk, res, register_ind[1], reg2);
//   register register2(clk, res, register_ind[2], reg3);
//   register register3(clk, res, register_ind[3], reg4);
//   mux mux0(reg1, reg2, reg3, reg4, addr, data);
  










  // wire [3:0] we_data = 4'b1111;
  // wire [3:0] rd_data;
  // wire we = 0;
  // wire [3:0] plus;
  // wire [3:0] minus;
  // wire [3:0] res;

  // wire [3:0] reg0;
  // wire [3:0] reg1;
  // wire [3:0] reg2;
  // wire [3:0] reg3;

  // register register0(clk, we_data, we, reg0);
  // register register1(clk, we_data, we, reg1);
  // register register2(clk, we_data, we, reg2);
  // register register3(clk, we_data, we, reg3);
  // mux mux0(reg0, reg1, reg2, reg3, addr, rd_data);
  
  // // register_file register_file1(clk, addr, addr, we_data, rd_data, we);
  
  // summ summ0(rd_data,immediate,plus);
  // substance substance0(rd_data,immediate,minus);
  
  // pmos pmos0(res[0],plus[0],control);
  // pmos pmos1(res[1],plus[1],control);
  // pmos pmos2(res[2],plus[2],control);
  // pmos pmos3(res[3],plus[3],control);

  // nmos nmos0(res[0],minus[0],control);
  // nmos nmos1(res[1],minus[1],control);
  // nmos nmos2(res[2],minus[2],control);
  // nmos nmos3(res[3],minus[3],control);

  // register_file register_file2(clk, addr, addr, res, data, we);
