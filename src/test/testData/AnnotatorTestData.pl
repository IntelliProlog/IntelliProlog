factorial(0<info descr="Binary operator">,</info> 1).
factorial(N<info descr="Binary operator">,</info> F) <info descr="Binary operator">:-</info>
                  N<info descr="Binary operator">></info>0<info descr="Binary operator">,</info>
                  N1 <info descr="Binary operator">is</info> N <info descr="Binary operator">-</info> 1<info descr="Binary operator">,</info>
                  factorial(N1<info descr="Binary operator">,</info> F1)<info descr="Binary operator">,</info>
                  F <info descr="Binary operator">is</info> F1 <info descr="Binary operator">*</info> N.

a <info descr="Binary operator">:-</info>
   b <info descr="Binary operator">=</info> [1<info descr="Binary operator">,</info>2<info descr="Binary operator">,</info>3<info descr="Binary operator">,</info>4<info descr="Binary operator">,</info>5<info descr="Binary operator">,</info>6<info descr="Binary operator">,</info>7<info descr="Binary operator">,</info>8<info descr="Binary operator">,</info>9<info descr="Binary operator">,</info>0].
