"use strict"
const varInd = {'x': 0, 'y': 1, 'z': 2};
const consts = {'pi': Math.PI, 'e': Math.E};

const cnst = value => () => value;
const variable = name => (...args) => args[varInd[name]];
const expression = f => {
    let result = (...subexpr) => (...args) => f(...subexpr.map((subexpr) => subexpr(...args)));
    result.arity = f.length;
    return result;
}

const add = expression((x, y) => x + y);
const subtract = expression((x, y) => x - y);
const multiply = expression((x, y) => x * y);
const divide = expression((x, y) => x / y);
const negate = expression((x) => -x);
const avg3 = expression((x, y, z) => (x + y + z) / 3);
const med5 = expression((a, b, c, d, e) => [a, b, c, d, e].sort((a, b) => a - b)[2]);
const pi = cnst(Math.PI);
const e = cnst(Math.E);

const exprMap = {
    '+': add,
    '-': subtract,
    '*': multiply,
    '/': divide,
    'negate': negate,
    'avg3': avg3,
    'med5': med5
};

const parse = str  => {
    let stack = [];
    str = str.split(' ').filter(s => s.length > 0);
    for (const token of str) {
        if (token in varInd) {
            stack.push(variable(token));
        } else if (token in consts) {
            stack.push(cnst(consts[token]));
        } else if (token in exprMap) {
            stack.push(exprMap[token](...stack.splice(-1 * exprMap[token].arity)));
        } else {
            stack.push(cnst(parseInt(token)));
        }
    }
    
    return stack.pop();
};

/*
for (let i = 0; i <= 10; i++) {
    let expr = add(subtract(multiply(variable('x'), variable('x')), multiply(cnst(2), variable('x'))), cnst(1));
    console.log(expr(i));
}
*/
