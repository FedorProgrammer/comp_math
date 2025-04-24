import numpy as np
import matplotlib.pyplot as plt
from math import factorial

# исходные данные
X = np.array([1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0,
              10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0])
Y = np.array([1.2998, -0.6984, -0.9958, -0.8038, -0.7053, -0.8225,
              -1.0023, -0.9969, -0.6222, 0.1277, 1.0290, 1.6596,
              1.5334, 0.3572, -1.5693, -2.7917, 0.0435])

def lagrange_basis(x, i, xi):
    prod = 1.0
    for j in range(len(x)):
        if j != i:
            prod *= (xi - x[j]) / (x[i] - x[j])
    return prod

def lagrange_interpolation(x, y, xi):
    return sum(y[i] * lagrange_basis(x, i, xi) for i in range(len(x)))

def compute_finite_differences(y):
    n = len(y)
    diff = [[0.0]*n for _ in range(n)]
    diff[0] = y.tolist()
    for k in range(1, n):
        for i in range(n - k):
            diff[k][i] = diff[k-1][i+1] - diff[k-1][i]
    return diff

def first_newton_interpolation(x, y, xi):
    n = len(x)
    h = x[1] - x[0]
    fd = compute_finite_differences(y)
    result, prod = y[0], 1.0
    for k in range(1, n):
        prod *= (xi - x[k-1])
        result += (fd[k][0] / (factorial(k) * h**k)) * prod
    return result

def second_newton_interpolation(x, y, xi):
    n = len(x)
    h = x[1] - x[0]
    fd = compute_finite_differences(y)
    result, prod = y[-1], 1.0
    for k in range(1, n):
        prod *= (xi - x[-k])
        result += (fd[k][n-k-1] / (factorial(k) * h**k)) * prod
    return result

if __name__ == "__main__":
    xs = np.linspace(X[0], X[-1], 600)  # больше точек для сглаженности
    y_lag = [lagrange_interpolation(X, Y, xi) for xi in xs]
    y_new_f = [first_newton_interpolation(X, Y, xi) for xi in xs]
    y_new_b = [second_newton_interpolation(X, Y, xi) for xi in xs]

    # --- График Лагранжа ---
    plt.figure(figsize=(8, 6), dpi=150)
    plt.plot(X, Y, 'o', color='black', label='Исходные точки')
    plt.plot(xs, y_lag, color='blue', label='Лагранж', linewidth=2)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Интерполяция Лагранжа')
    plt.legend()
    plt.grid(True)
    plt.savefig("lagrange.png")
    plt.close()

    # --- График Ньютона: прямой ход ---
    plt.figure(figsize=(8, 6), dpi=150)
    plt.plot(X, Y, 'o', color='black', label='Исходные точки')
    plt.plot(xs, y_new_f, color='green', label='Ньютон вперёд', linewidth=2)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Интерполяция Ньютона (вперёд)')
    plt.legend()
    plt.grid(True)
    plt.savefig("newton_forward.png")
    plt.close()

    # --- График Ньютона: обратный ход ---
    plt.figure(figsize=(8, 6), dpi=150)
    plt.plot(X, Y, 'o', color='black', label='Исходные точки')
    plt.plot(xs, y_new_b, color='red', label='Ньютон назад', linewidth=2)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Интерполяция Ньютона (назад)')
    plt.legend()
    plt.grid(True)
    plt.savefig("newton_backward.png")
    plt.close()
    
    # --- График Линейной аппроксимации ---
    plt.figure(figsize=(8, 6), dpi=150)
    # коэффициенты из Java: y = a*x + b
    a, b = -0.0018411764705882345, -0.2750764705882353
    y_lin = a * xs + b
    plt.plot(X, Y, 'o', color='black', label='Исходные точки')
    plt.plot(xs, y_lin, color='purple',
             label=f'Линейная: y = {a:.10f}x + ({b:.10f})',
             linewidth=2)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Линейная аппроксимация')
    plt.legend()
    plt.grid(True)
    plt.savefig("linear_approx.png")
    plt.close()
    
    # --- Объединённый график регрессий на одном полотне ---
    plt.figure(figsize=(8, 6), dpi=150)
    # точки данных
    plt.plot(X, Y, 'o', color='black', label='Точки данных')
    # регрессия Y(X): y = a*x + b
    a_yx, b_yx = -0.001956249999999999, -0.27404080882352944
    y_reg = a_yx * xs + b_yx
    plt.plot(xs, y_reg, color='orange',
             label=f'Y(X): y = {a_yx:.12f}x + ({b_yx:.12f})',
             linewidth=2)
    # регрессия X(Y): x = c*y + d, выводим как x_reg vs y
    c_xy, d_xy = -0.0354812462467747, 8.989651998888736
    ys = np.linspace(Y.min(), Y.max(), 600)
    x_reg = c_xy * ys + d_xy
    plt.plot(x_reg, ys, color='brown',
             label=f'X(Y): x = {c_xy:.12f}y + ({d_xy:.12f})',
             linewidth=2)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Сравнение регрессий Y(X) и X(Y)')
    plt.legend()
    plt.grid(True)
    plt.savefig("regressions.png")
    plt.close()