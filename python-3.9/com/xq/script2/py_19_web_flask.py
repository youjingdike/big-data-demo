#!/usr/bin/env python
# coding=utf-8
from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return 'Hello, Flask!'

@app.route('/a')
def helloa():
    return 'Hello, a!'

if __name__ == '__main__':
    app.run()
