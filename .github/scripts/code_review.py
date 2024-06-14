import os
import requests
import subprocess
import sys
import logging
import protobuf
import sentencepiece
from transformers import AutoTokenizer, AutoModelForCausalLM
import torch
from huggingface_hub import login

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def install_sentencepiece():
    logger.info("Installing sentencepiece")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "sentencepiece"])
    logger.info("sentencepiece installed successfully")



# Load tokens from environment variables
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
HUGGINGFACE_HUB_TOKEN = os.getenv('HUGGINGFACE_HUB_TOKEN')
MISTRAL_MODEL_NAME = 'mistralai/mistral-7b-instruct-v0.3'

# Ensure the Hugging Face token is available
if not HUGGINGFACE_HUB_TOKEN:
    raise ValueError("HUGGINGFACE_HUB_TOKEN environment variable not set")

# Hugging Face Login
login(token=HUGGINGFACE_HUB_TOKEN)

# Load the Mistral model and tokenizer
try:
    tokenizer = AutoTokenizer.from_pretrained(MISTRAL_MODEL_NAME)
    model = AutoModelForCausalLM.from_pretrained(MISTRAL_MODEL_NAME)
except ValueError as e:
    logger.error(f"Error loading tokenizer: {e}")
    logger.info("Retrying with LlamaTokenizer")
    from transformers import LlamaTokenizer
    try:
        tokenizer = LlamaTokenizer.from_pretrained(MISTRAL_MODEL_NAME)
        model = AutoModelForCausalLM.from_pretrained(MISTRAL_MODEL_NAME)
    except ImportError:
        install_sentencepiece()
        import importlib
        importlib.invalidate_caches()
        import sentencepiece
        tokenizer = LlamaTokenizer.from_pretrained(MISTRAL_MODEL_NAME)
        model = AutoModelForCausalLM.from_pretrained(MISTRAL_MODEL_NAME)

def get_pr_files(repo, pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {'Authorization': f'token {GITHUB_TOKEN}'}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def call_mistral_llm(filename, patch):
    prompt = f"Review the following code changes in {filename}:\n{patch}\nProvide any suggestions or improvements on the basis of security, logic errors."

    inputs = tokenizer(prompt, return_tensors="pt")
    outputs = model.generate(**inputs, max_new_tokens=750)
    review_comment = tokenizer.decode(outputs[0], skip_special_tokens=True)

    return review_comment

def post_review_comments(repo, pr_number, comments):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/reviews"
    headers = {'Authorization': f'token {GITHUB_TOKEN}'}
    data = {
        'body': 'Automated code review comments',
        'event': 'COMMENT',
        'comments': comments
    }
    response = requests.post(url, json=data, headers=headers)
    response.raise_for_status()
    return response.json()

def main():
    repo = os.getenv('GITHUB_REPOSITORY')
    pr_number = os.getenv('PR_NUMBER')

    files = get_pr_files(repo, pr_number)
    comments = []
    for file in files:
        filename = file['filename']
        patch = file.get('patch', '')
        if patch:
            review_comment = call_mistral_llm(filename, patch)
            if review_comment:
                comments.append({
                    'path': filename,
                    'position': 1,
                    'body': review_comment
                })

    post_review_comments(repo, pr_number, comments)

if __name__ == "__main__":
    main()

