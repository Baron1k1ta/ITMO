module ternary_min(a,b,out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire or1;
  wire or2;
  wire or3;
  wire and1;
  and_gate and_gate1 (a[1],b[1],out[1]);
  or_gate or_gate1 (a[0],b[0],or1);
  or_gate or_gate2 (a[0],a[1],or2);
  or_gate or_gate3 (b[0],b[1],or3);
  and_gate and_gate2 (or1,or2,and1);
  and_gate and_gate3 (and1,or3,out[0]);
endmodule

module ternary_max(a,b,out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire or1;
  supply0 gnd;
  or_gate or_gate1(a[1],b[1],out[1]);
  or_gate or_gate2(a[0],b[0],or1);
  nmos nmos1(out[0],gnd,out[1]);
  pmos pmos1(out[0],or1,out[1]);

endmodule

module ternary_any(a,b,out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire or1;
  wire or2;
  wire or3;
  wire and0;
  wire and1;
  wire and2;
  wire and3;
  wire and4;
  wire and5;
  wire and6;
  wire and7;
  wire and8;
  wire xor1;
  or_gate or_gate1(a[0],a[1],or1);
  or_gate or_gate2(b[0],b[1],or2);
  or_gate or_gate3(a[1],b[1],or3);
  and_gate and_gate0(or1,or2,and0);

  and_gate and_gate1(and0,or3,out[1]);
  and_gate and_gate2(b[0],b[1],and1);
  and_gate and_gate3(a[0],b[1],and2);
  and_gate and_gate4(b[0],a[1],and3);
  and_gate and_gate5(a[0],a[1],and4);
  and_gate and_gate6(a[0],b[0],and5);
  and_gate and_gate7(and5,b[1],and6);
  and_gate and_gate8(and5,and4,and7);
  and_gate and_gate9(and5,a[1],and8);
  xor_gate xor_gate1(a[1],b[1],xor1);
  xor_gate xor_gate2(xor1,and1,xor2);
  xor_gate xor_gate3(xor2,and2,xor3);
  xor_gate xor_gate4(xor3,and3,xor4);
  xor_gate xor_gate5(xor4,and4,xor5);
  xor_gate xor_gate6(xor5,and6,xor6);
  xor_gate xor_gate7(xor6,and7,xor7);
  xor_gate xor_gate8(xor7,and5,xor8);
  xor_gate xor_gate9(xor8,and8,out[0]);

endmodule

module ternary_consensus(a,b,out);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire or1;
  wire or2;
  wire or3;
  supply0 gnd;
  and_gate and_gate1(a[1],b[1],out[1]);
  or_gate or_gate1(a[0],b[0],or1);
  or_gate or_gate2(a[1],b[1],or2);
  or_gate or_gate3(or1,or2,or3);
  nmos nmos1(out[0],gnd,out[1]);
  pmos pmos1(out[0],or3,out[1]);
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