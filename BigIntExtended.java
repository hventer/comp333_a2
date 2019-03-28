/* Template for class BigIntExtended (Assign 2).
 * Updated on 31 October 2018.
 * This class is intended to extend the functionality provided 
 * by class BigInt.
 * This template is provided as part of Assignment 2 for COMP333.
 * Every JUnit test routine used in marking each method you write
 * will conform to the stated precondition and description of
 * output for the method.
 */
package comp333;

public class BigIntExtended {

	// Euclid's algorithm, extended form.
	// Pre: a and b are positive integers.
	// Returns: triple (d, x, y) such that
	//          d = gcd(a,b) = ax + by.
	public static int [] egcd(int a, int b) {
		int [] result = new int[3];

		//the columns in which x and y are found
		int colA[]={1,0};
		int colB[]={0,1};

		while(a >= 0 && b >= 0) {		
			// q is quotient 
			int q = 0;

			q = a / b;
			a = a % b;

			colA[0] = colA[0] - q*colA[1];
			colB[0] = colB[0] - q*colB[1];

			if (a == 0) {
				break;
			}

			q = b / a;
			b = b % a;

			colA[1] = colA[1] - q*colA[0];
			colB[1] = colB[1] - q*colB[0];

			if (b == 0) {
				break;
			}
		}

		if (a == 0) {
			result[0] = b;
			result[1] = colA[1];
			result[2] = colB[1];
			return result;
		}

		else {
			result[0] = a;
			result[1] = colA[0];
			result[2] = colB[0];
			return result;
		}
	}		


	// Modular inversion.
	// Pre: a and n are positive integers which are coprime.
	// Returns: the inverse of a modulo n.
	//          The inverse of a modulo n should be an element
	//          of Z_n = {0,1,...,n-1}.
	public static int minv(int a, int n) {
		if (n == 1) 
			return 0;

		int [] gcd = egcd(a, n);
		int x = gcd[1];

		// Make x positive 
		if (x < 0) 
			x += n; 

		return x; 
	}

	// Chinese remainder algorithm.
	// Pre: p and q are different prime numbers.
	//      0 <= a < p and 0 <= b < q.
	// Returns: the unique integer c such that
	//          c is congruent to a modulo p,
	//          c is congruent to b modulo q,
	//          and 0 <= c < pq.
	public static int cra(int p, int q, int a, int b) {
		int c = 0;

		int k = (b - a) * (minv(p, q) % q);
		c = a + k * p;

		return c;
	}

	// Modular exponentiation.
	// Pre: a and b are nonnegative big integers.
	//      n is a positive big integer.
	// Returns: this method returns a^b mod n.
	public static BigInt modexp(BigInt a, BigInt b, BigInt n) {
		BigInt y = new BigInt(1);

		//this is the remainder from mod
		BigInt z = a.divide(n)[1];
		BigInt i = b;

		while(!i.lessOrEqual(new BigInt())) {
			//find i mod 2, to check if its even
			//if the remained !equal to 0, its odd
			if(!i.divide(new BigInt(2))[1].isEqual(new BigInt())) {
				y = z.multiply(y);
				y = y.divide(n)[1];
			}

			z = z.multiply(z);
			z = z.divide(n)[1];

			i = i.divide(new BigInt(2))[0];
		}
		return y;
	}

	// Modular exponentiation, version 2.
	// This method returns a^b mod n, in case n is prime.
	// (An improvement to the standard efficient mod exp
	// algorithm is requested for this special case.
	// As a hint, try to reduce the size of the exponent b
	// right at the beginning.)
	public static BigInt modexpv2(BigInt a, BigInt b, BigInt n) {
		// reduce b using fermat's little theorem
		BigInt nReduced = n.subtract(new BigInt(1));
		BigInt fermat = nReduced.multiply(b.divide(nReduced)[0]);
		b = b.subtract(fermat);
		return modexp(a, b, n);
	}

	// Modular exponentiation, version 3.
	// This method returns a^b mod n, in case n = pq, for different
	// primes p and q.
	// (An improvement to the standard efficient mod exp
	// algorithm is requested for this special case.
	// As a hint, use the Chinese remainder algorithm.
	//TODO
	public static int modexpv3(int a, int b, int p, int q) {
		//find a^b mod p
		int p1 = 1;
		int z = a % p;
		int i = b;
		while (i > 0) {
			if (i%2 != 0)
				p1 = z * p1 % p;

			z = z * z % p;
			i = i / 2;
		}

		//find a^b mod q
		int q1 = 1;
		z = a % q;
		i = b;
		while (i > 0) {
			if (i%2 != 0)
				q1 = z * q1 % q;

			z = z * z % q;
			i = i / 2;
		}
		
		int c = cra(a,b,p1,q1);

		return c;
	}

	// Pre: a and b are nonnegative big integers of equal length.
	// Returns: the product of a and b using karatsuba's algorithm.
	//TODO
	public static BigInt karatsuba(BigInt a, BigInt b) {
		BigInt c = new BigInt();

		//stopping case
		if(a.toString().length() == 1) {
			c = a.multiply(b); //compute double prec. product
			return c;
		}

		String aString = a.toString();
		String bString = b.toString();

		int mid = (aString.length())/2;

		BigInt a1 = new BigInt(aString.substring(0, mid));
		BigInt a0 = new BigInt(aString.substring(mid));
		BigInt b1 = new BigInt(bString.substring(0, mid));
		BigInt b0 = new BigInt(bString.substring(mid));

		BigInt as = a1.add(a0);
		BigInt bs = b1.add(b0);

		BigInt c2 = karatsuba(a1, b1);
		BigInt c0 = karatsuba(a0, b0);
		BigInt cs = karatsuba(as, bs);

		BigInt s = c0.add(c2);
		BigInt c1 = cs.subtract(s);

		Integer aL = power(10, aString.length());
		BigInt cP2 = c2.multiply(new BigInt(aL.toString()));

		aL = power(10, aString.length()/2);
		BigInt cP1 = c1.multiply(new BigInt(aL.toString()));

		c = cP2.add(cP1.add(c0));

		return c;
	}


	public static Integer power(int x, int y) {
		if (y == 0)
			return 1;
		else {
			Integer answer = 1;
			for (int i = 1; i<=y; i++) {
				answer *= x;
			}
			return answer;
		}
	}



	// Pre: n is an odd big integer greater than 4.
	//      s is an ordinary positive integer.
	// Returns: if n is prime then returns true with certainty,
	//          otherwise returns false with probability
	//          1 - 4^{-s}.
	//TODO
	public static boolean millerrabin(BigInt n, int s) {
		randomprime(n.toString().length(), s);


		// Complete this code.

		return true;
	}

	// Pre: l and s are ordinary positive integers.
	// Returns: a random "probable" big prime number n of length l decimal digits.
	//          The probability that n is not prime is less than 4^{-s}.
	//TODO
	public static BigInt randomprime(int l, int s) {
		BigInt result = new BigInt();

		// Complete this code.

		return result;
	}

	public static void main(String[] args) {

		// Provide a simple interactive demo of the RSA
		// cryptosystem. You may use ordinary Java int
		// values in your demo. (that is, you do not have to
		// use big integers.
	}


}
