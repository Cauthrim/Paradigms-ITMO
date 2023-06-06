"use strict"

function createError(name, message) {
	let err = function() {
		this.message = message;
	}
	err.prototype = Object.create(Error.prototype);
	err.prototype.constructor = err;
	err.prototype.name = name;
	return err;
}

const InvalidNumberError = createError('InvalidNumberError', 'Invalid number');
const InvalidExpressionError = createError('InvalidExpressionError', 'Invalid expression');
const UnknownOperationError = createError('UnknownOperationError', 'Unknown operation');
const InvalidArgumentsError = createError('InvalidArgumentsError', 'Invalid arguments');

function assert(clause, error) {
	if (!clause) {
		throw new error();
	}
}

const varInd = {'x': 0, 'y': 1, 'z': 2};

function createVal(evaluate, name, toString, diff) {
	const valConstr = function(val) {
		this.value = val;
	};
	valConstr.prototype.name = name;
	valConstr.prototype.constructor = valConstr;
	valConstr.prototype.evaluate = evaluate;
	valConstr.prototype.prefix = toString;
	valConstr.prototype.postfix = toString;
	valConstr.prototype.toString = toString;
	valConstr.prototype.diff = diff;
	return valConstr;
}

const Const = createVal(
	function(...args) {return parseInt(this.value);},
	'Const',
 	function() {return this.value.toString();},
 	function(...args) {return zero;});

const Variable = createVal(
	function(...args) {return args[varInd[this.value]];},
	'Variable',
 	function() {return this.value;},
 	function(...args) {return args[0] === this.value ? one : zero;});

const zero = new Const(0);
const one = new Const(1);
const two = new Const(2);

function createOper(exprSym, name, f, diff) {
	const operExpr = function(...args) {
		this.subexpr = args;
	}

	operExpr.prototype.name = name;
	operExpr.prototype.constructor = operExpr;

	operExpr.prototype.evaluate = function(...args) {
		return f(...this.subexpr.map(function(subexpr) {return subexpr.evaluate(...args);}));
	};

	operExpr.arity = f.length;

	operExpr.prototype.prefix = function() {
		let res = '(' + exprSym;
		for (const expr of this.subexpr) {
			res += ' ' + expr.prefix(); 
		}
		if (this.subexpr.length === 0) {
			res += ' ';
		}
		res += ')';
		return res;
	}

	operExpr.prototype.postfix = function() {
		let res = '(';
		for (const expr of this.subexpr) {
			res += expr.postfix() + ' '; 
		}
		if (this.subexpr.length === 0) {
			res += ' ';
		}
		res += exprSym + ')';
		return res;
	}

	operExpr.prototype.toString = function() {
		let res = '';
		for (const expr in this.subexpr) {
			res += this.subexpr[expr].toString() + ' ';
		}
		res += exprSym;
		return res;
	};

	operExpr.prototype.diff = function(vars) {
		return diff(vars, this.subexpr, this.subexpr.map(function(arg) {return arg.diff(...vars);}));
	};
    return operExpr;
}

const Add = createOper('+', 'Add',
	function(x, y) {return x + y;},
	function(vars, subexpr, diffs) {return new Add(diffs[0], diffs[1]);});

const Subtract = createOper('-', 'Subtract',
	function(x, y) {return x - y;},
	function(vars, subexpr, diffs) {return new Subtract(diffs[0], diffs[1]);});

const Multiply = createOper('*', 'Multiply',
	function(x, y) {return x * y;},
	function(vars, subexpr, diffs) {
	 	return new Add(new Multiply(diffs[0], subexpr[1]), new Multiply(subexpr[0], diffs[1]));
	});

const Divide = createOper('/', 'Divide',
	function(x, y) {return x / y;},
	function(vars, subexpr, diffs) {
	 	return new Divide(new Subtract(new Multiply(diffs[0], subexpr[1]),
	  	new Multiply(subexpr[0], diffs[1])), new Multiply(subexpr[1], subexpr[1]));
	});

const Negate = createOper('negate', 'Negate',
	function(x) {return -x;},
	function(vars, subexpr, diffs) {return new Negate(diffs[0]);});

const Exp = createOper('exp', 'Exp', 
	function(x) {return Math.exp(x);},
	function(vars, subexpr, diffs) {return new Multiply(diffs[0], new Exp(subexpr[0]));});

const Gauss = createOper('gauss', 'Gauss',
	function(a, b, c, x) {return a * Math.exp(-(x - b) * (x - b) / 2 / c / c);},
	function(vars, subexpr, diffs) {
		const a = subexpr[0];
		const b = subexpr[1];
		const c = subexpr[2];
		const x = subexpr[3];
		return new Multiply(a, new Exp(new Negate(new Divide(new Multiply(new Subtract(x, b), new Subtract(x, b)),
		new Multiply(two, new Multiply(c, c)))))).diff(...vars);
	});

const Sumexp = createOper('sumexp', 'Sumexp',
	function(...args) {
		let res = 0;
		for (const arg of args) {
			res += Math.exp(arg);
		}
		return res;
	},
	function(vars, subexpr, diffs) {
		let stack = [];
		for (const expr of subexpr) {
			stack.push(new Exp(expr));
		}
		while (stack.length > 1) {
			stack.push(new Add(...stack.splice(-2)));
		}
		return stack.pop().diff(...vars);
	});

const Softmax = createOper('softmax', 'Softmax',
	function(...args) {
		assert(args.length > 0, InvalidArgumentsError);
		let div = 0;
		for (const arg of args) {
			div += Math.exp(arg);
		}
		return Math.exp(args[0]) / div;
	},
	function(vars, subexpr, diffs) {
		return new Divide(new Exp(subexpr[0]), new Sumexp(...subexpr)).diff(...vars);
	});

const exprMap = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'gauss': Gauss,
    'sumexp': Sumexp,
    'softmax': Softmax
};

const parse = str  => {
    let stack = [];
    str = str.split(' ').filter(s => s.length > 0);
    for (const token of str) {
        if (token in varInd) {
            stack.push(new Variable(token));
        } else if (token in exprMap) {
            stack.push(new exprMap[token](...stack.splice(-1 * exprMap[token].arity)));
        } else {
            stack.push(new Const(token));
        }
    }
    
    return stack.pop();
};

function altParse (str, isPost) {
	let valStack = [];
	let funcStack = [];
	let arityStack = [];

	for (let ind = 0; ind < str.length; ind++) {
		if (str[ind] === '(') {
			assert(ind < str.length-1, InvalidExpressionError);
			arityStack.push(valStack.length);

			assert(str[ind+1] in exprMap, UnknownOperationError);
			funcStack.push(str[++ind]);
		} else if (str[ind] === ')'){
			assert(funcStack.length > 0, InvalidExpressionError);
			assert(exprMap[funcStack.slice(-1)].arity === 0 
				|| valStack.length-arityStack.slice(-1) === exprMap[funcStack.slice(-1)].arity, InvalidArgumentsError);

			const argNum = -1 * (valStack.length - arityStack.pop());
			let args = [];
			if (argNum !== 0) {
				args = valStack.splice(argNum);
			}
			if (isPost === true) {
				args = args.reverse();
			}
			valStack.push(new exprMap[funcStack.pop()](...args));
		} else {
			if (str[ind] in varInd) {
				valStack.push(new Variable(str[ind]));
			} else {
				assert(!isNaN(str[ind]), InvalidNumberError);
				valStack.push(new Const(str[ind]));
			}
		}
	}
	assert(funcStack.length === 0 && valStack.length === 1, InvalidExpressionError);

	return valStack.pop();
}

function processStr(str) {
	str = str.replace(/\(|\)/g, function(seq) {return ' ' + seq + ' ';});
	return str.split(' ').filter(s => s.length > 0);
}

const parsePrefix = str => {
	return altParse(processStr(str), false);
}

const parsePostfix = str => {
	str = processStr(str);

	for (let i = 0; i < str.length; i++) {
		if (str[i] === '(') {
			str[i] = ')';
		} else if (str[i] === ')') {
			str[i] = '(';
		}
	}

	return altParse(str.reverse(), true);
}