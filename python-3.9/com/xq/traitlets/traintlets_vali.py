#!/usr/bin/python
# -*- coding: UTF-8 -*-
from traitlets import HasTraits, TraitError, Int, Bool, validate, Dict, Unicode, default

print("Custom Cross-Validation...")
# Basic Example: Validating the Parity of a Trait

class Parity(HasTraits):
    data = Int()
    parity = Int()

    @validate("data")
    def _valid_data(self, proposal):
        if proposal["value"] % 2 != self.parity:
            raise TraitError("data and parity should be consistent")
        return proposal["value"]

    @validate("parity")
    def _valid_parity(self, proposal):
        parity = proposal["value"]
        if parity not in [0, 1]:
            raise TraitError("parity should be 0 or 1")
        if self.data % 2 != parity:
            raise TraitError("data and parity should be consistent")
        return proposal["value"]


parity_check = Parity(data=2)

# Changing required parity and value together while holding cross validation
with parity_check.hold_trait_notifications():
    parity_check.data = 5
    parity_check.parity = 1

# Advanced Example: Validating the Schema
print("#######")
class Nested(HasTraits):
    value = Dict(
        per_key_traits={"configuration": Dict(value_trait=Unicode()), "flag": Bool()}
    )


n = Nested()
n.value = dict(flag=True, configuration={})  # OK
# n.value = dict(flag=True, configuration="")  # raises a TraitError.

print("#########external validator##########")

import jsonschema

value_schema = {
    "type": "object",
    "properties": {
        "price": {"type": "number"},
        "name": {"type": "string"},
    },
}

class Schema(HasTraits):
    value = Dict()

    @default("value")
    def _default_value(self):
        return dict(name="", price=1)

    @validate("value")
    def _validate_value(self, proposal):
        try:
            jsonschema.validate(proposal["value"], value_schema)
        except jsonschema.ValidationError as e:
            raise TraitError(e)
        return proposal["value"]


s = Schema()
# s.value = dict(name="", price="1")  # raises a TraitError
s.value = dict(name="", price=2)